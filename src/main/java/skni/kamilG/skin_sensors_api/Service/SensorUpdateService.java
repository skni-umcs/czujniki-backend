package skni.kamilG.skin_sensors_api.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import skni.kamilG.skin_sensors_api.Exception.SensorUpdateException;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.Mapper.SensorMapper;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorStatus;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorUpdateFailure;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorUpdateFailureRepository;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Executes aync process of updating all sensors current data. @See ISensorUpdateService for
 * definition of the methods
 */
@Slf4j
@Service
@AllArgsConstructor
public class SensorUpdateService implements ISensorUpdateService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;
  private final SensorUpdateFailureRepository sensorUpdateFailureRepository;
  private final Sinks.Many<SensorResponse> sensorUpdatesSink;
  private final SensorMapper sensorMapper;
  private final Clock clock;
  private final InfluxService influxService;
  private final Duration heartbeatInterval;

  @Override
  public void forceUpdateSensorsData() {
    performSensorDataUpdate();
  }

  @Async("virtualThreadExecutor")
  @Scheduled(fixedRate = 60000)
  @Override
  public void updateSensorsData() {
    try {
      List<SensorResponse> updatedSensors = performSensorDataUpdate();
      for (SensorResponse sensor : updatedSensors) {
        sensorUpdatesSink.emitNext(sensor, (signalType, emitResult) -> {
          if (emitResult.isFailure()) {
            log.warn("Failed to emit sensor update: {} - {}", emitResult, signalType);
          }
          return false;
        });
      }
      log.debug("Emitted {} sensor updates to SSE stream", updatedSensors.size());
    } catch (Exception e) {
      log.error("Error during sensor update: {}", e.getMessage(), e);
    }
  }

  @Override
  public List<SensorResponse> performSensorDataUpdate() {
    log.info("Starting sensor data update process");
    influxService.fetchLatestData();

    List<Sensor> sensorsToUpdate = findSensorsToUpdate();
    Map<Short, SensorData> latestData = fetchLatestSensorData(sensorsToUpdate);

    List<SensorResponse> updatedSensors = new ArrayList<>();
    List<SensorUpdateFailure> updateFailures = new ArrayList<>();

    processAndUpdateSensors(sensorsToUpdate, latestData, updatedSensors, updateFailures);

    saveUpdateFailures(updateFailures);

    log.info("Sensor data update process completed");
    return updatedSensors;
  }

  private List<Sensor> findSensorsToUpdate() {
    List<Sensor> sensors = sensorRepository.findByStatusNotOrderById(SensorStatus.OFFLINE);
    log.info(
        "Found {} sensors to update with IDs: {}",
        sensors.size(),
        sensors.stream().map(Sensor::getId).collect(Collectors.toList()));
    return sensors;
  }

  private Map<Short, SensorData> fetchLatestSensorData(List<Sensor> sensors) {
    List<Short> sensorIds = sensors.stream().map(Sensor::getId).collect(Collectors.toList());

    return sensorDataRepository.findLatestDataBySensorIds(sensorIds).stream()
        .collect(Collectors.toMap(data -> data.getSensor().getId(), Function.identity()));
  }

  private void processAndUpdateSensors(
      List<Sensor> sensors,
      Map<Short, SensorData> latestData,
      List<SensorResponse> updatedSensors,
      List<SensorUpdateFailure> updateFailures) {
    LocalDateTime thresholdTime = LocalDateTime.now(clock).minusSeconds(60);

    for (Sensor sensor : sensors) {
      try {
        processIndividualSensor(sensor, latestData, thresholdTime, updatedSensors, updateFailures);
      } catch (Exception e) {
        log.error("Error updating sensor id: {} data", sensor.getId(), e);
        throw new SensorUpdateException(e);
      }
    }
  }

  private void processIndividualSensor(
      Sensor sensor,
      Map<Short, SensorData> latestData,
      LocalDateTime thresholdTime,
      List<SensorResponse> updatedSensors,
      List<SensorUpdateFailure> updateFailures) {
    SensorData latestSensorData = latestData.get(sensor.getId());

    if (latestSensorData == null) {
      handleMissingSensorData(sensor, updateFailures);
    } else {
      if (isDataOutdated(latestSensorData, thresholdTime)) {
        handleOutdatedSensorData(sensor, latestSensorData, updateFailures);
        return;
      }
    }

    updateSensorSuccessfully(sensor, latestSensorData, updatedSensors);
  }

  private boolean isDataOutdated(SensorData sensorData, LocalDateTime thresholdTime) {
    return !sensorData.getTimestamp().isAfter(thresholdTime);
  }

  private void handleMissingSensorData(Sensor sensor, List<SensorUpdateFailure> updateFailures) {
    String warning =
        String.format("Sensor %d has no latest data, setting status to ERROR", sensor.getId());
    log.warn(warning);

    sensor.setStatus(SensorStatus.ERROR);
    updateFailures.add(new SensorUpdateFailure(LocalDateTime.now(clock), warning, sensor));
    sensorRepository.save(sensor);
  }

  private void handleOutdatedSensorData(
      Sensor sensor, SensorData latestSensorData, List<SensorUpdateFailure> updateFailures) {
    String warning =
        String.format(
            "Sensor %d has outdated data (last updated at %s), setting status to ERROR",
            sensor.getId(), latestSensorData.getTimestamp());
    log.warn(warning);
    sensor.setStatus(SensorStatus.ERROR);
    updateFailures.add(new SensorUpdateFailure(LocalDateTime.now(clock), warning, sensor));
    sensorRepository.save(sensor);
  }

  private void updateSensorSuccessfully(
      Sensor sensor, SensorData latestSensorData, List<SensorResponse> updatedSensors) {
    sensor.updateFromSensorData(latestSensorData, clock);
    if (sensor.getStatus() == SensorStatus.ERROR) {
      log.info("Sensor id: {} has recovered from error state", sensor.getId());
      SensorUpdateFailure tmp =
          sensorUpdateFailureRepository.getBySensorIdAndResolvedTimeIsNull(sensor.getId());
      tmp.setResolvedTime(LocalDateTime.now(clock));
      sensorUpdateFailureRepository.save(tmp);
    }
    sensor.setStatus(SensorStatus.ONLINE);
    updatedSensors.add(sensorMapper.createSensorToSensorResponse(sensor));
    log.info("Updated sensor id: {} with latest data", sensor.getId());
    sensorRepository.save(sensor);
  }

  private void saveUpdateFailures(List<SensorUpdateFailure> updateFailures) {
    if (!updateFailures.isEmpty()) {
      List<SensorUpdateFailure> uniqueFailures = updateFailures.stream()
              .filter(failure -> !sensorUpdateFailureRepository.existsBySensorIdAndResolvedTimeIsNull(failure.getSensor().getId()))
              .collect(Collectors.toList());
      if (!uniqueFailures.isEmpty()) {
        sensorUpdateFailureRepository.saveAll(uniqueFailures);
        log.warn("{} sensor update failures recorded", uniqueFailures.size());
      }
    }
  }

  @Override
  public Flux<ServerSentEvent<SensorResponse>> getAllSensorsUpdatesAsSSE() {
    Flux<ServerSentEvent<SensorResponse>> initialState = Flux.defer(() -> {
      List<SensorResponse> currentState = sensorRepository.findAll().stream()
              .map(sensorMapper::createSensorToSensorResponse)
              .collect(Collectors.toList());

      log.debug("New client connected, sending initial state of {} sensors", currentState.size());

      return Flux.fromIterable(currentState)
              .map(this::createServerSentEvent); //initial
    });

    Flux<ServerSentEvent<SensorResponse>> updates = sensorUpdatesSink.asFlux()
            .map(this::createServerSentEvent); //update

    Flux<ServerSentEvent<SensorResponse>> heartbeats = Flux.interval(heartbeatInterval)
            .map(tick -> ServerSentEvent.<SensorResponse>builder()
                    .id("heartbeat-" + System.currentTimeMillis())
                    .comment("heartbeat")
                    .build());

    return Flux.merge(initialState, updates, heartbeats)
            .onErrorResume(IOException.class, e -> {
              if (e.getMessage().contains("Broken pipe") ||
                      e.getMessage().contains("Connection reset by peer")) {
                log.debug("Client disconnected from SSE stream (normal behavior)");
                return Mono.empty();
              }
              log.warn("IO error in SSE stream: {}", e.getMessage());
              return Mono.error(e);
            })
            .onErrorResume(e -> {
              log.error("Unhandled error in SSE stream: {}", e.getMessage());
              return Mono.empty();
            })
            .timeout(Duration.ofMinutes(15));

  }

  @Override
  public Flux<ServerSentEvent<SensorResponse>> getSensorUpdatesAsSSE(Short sensorId) {
    return getAllSensorsUpdatesAsSSE()
            .filter(sse -> {
              if (sse.comment() != null && sse.comment().equals("heartbeat")) {
                return true;
              }

              SensorResponse data = sse.data();
              return data != null && data.id().equals(sensorId);
            });
  }

  private ServerSentEvent<SensorResponse> createServerSentEvent(SensorResponse sensor) {
    return ServerSentEvent.<SensorResponse>builder()
            .id(sensor.id() + "-" + System.currentTimeMillis())
            .data(sensor)
            .build();
  }
}
