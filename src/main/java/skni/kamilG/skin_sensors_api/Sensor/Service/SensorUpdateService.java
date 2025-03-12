package skni.kamilG.skin_sensors_api.Sensor.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import skni.kamilG.skin_sensors_api.Influx.IInfluxService;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.Mapper.SensorMapper;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorData;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorStatus;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorUpdateFailure;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorRepository;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorUpdateFailureRepository;

/**
 * Executes aync process of updating all sensors current data. @See ISensorUpdateService for
 * definition of the methods
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensorUpdateService implements ISensorUpdateService {

  private final SensorRepository sensorRepository;
  private final SensorUpdateFailureRepository sensorUpdateFailureRepository;
  private final SensorMapper sensorMapper;
  private final Sinks.Many<SensorResponse> sensorUpdatesSink;
  private final Duration heartbeatInterval;
  private final IInfluxService influxService;
  private final Clock clock;

  @Value("${scheduler.default-rate:180}")
  private short defaultRate;

  public List<Sensor> findSensorsToUpdate() {
    return sensorRepository.findByStatusNotOrderById(SensorStatus.OFFLINE);
  }

  public void updateSingleSensor(Sensor sensor) {
    Optional<SensorData> currentReading =
        influxService.fetchLatestData(
            sensor.getId(),
            sensor.getRefreshRate() == null ? defaultRate : sensor.getRefreshRate());
    if (currentReading.isPresent()) {
      SensorData sensorData = currentReading.get();
      sensor.updateFromSensorData(sensorData, clock);
      if (sensor.getStatus() == SensorStatus.ERROR) {
        recoverSensorFromError(sensor);
      }
      sensorRepository.save(sensor);
      sensorUpdatesSink.tryEmitNext(sensorMapper.mapToSensorResponse(sensor));
    } else {
      recordUpdateFailure(sensor);
    }
  }

  private void recordUpdateFailure(Sensor sensor) {
    if (sensorUpdateFailureRepository.existsBySensorIdAndResolvedTimeIsNull(sensor.getId())) {
      log.debug("Sensor {} already has unresolved update failure", sensor.getId());
      return;
    }
    String warning = String.format("Sensor %d didn't send latest data", sensor.getId());
    log.warn(warning);
    sensor.setStatus(SensorStatus.ERROR);

    sensorUpdateFailureRepository.save(
        new SensorUpdateFailure(ZonedDateTime.from(ZonedDateTime.now(clock)), warning, sensor));
    sensorRepository.save(sensor);
  }

  private void recoverSensorFromError(Sensor sensor) {
    sensor.setStatus(SensorStatus.ONLINE);
    SensorUpdateFailure failureToChange =
        sensorUpdateFailureRepository.getBySensorIdAndResolvedTimeIsNull(sensor.getId());
    failureToChange.setResolvedTime(ZonedDateTime.from(ZonedDateTime.now(clock)));
    sensorUpdateFailureRepository.save(failureToChange);
    log.info("Sensor {} is back online", sensor.getId());
  }

  @Override
  public Flux<ServerSentEvent<SensorResponse>> getAllSensorsUpdatesAsSSE() {
    Flux<ServerSentEvent<SensorResponse>> initialState =
        Flux.defer(
            () -> {
              List<SensorResponse> currentState =
                  sensorRepository.findAll().stream()
                      .map(sensorMapper::mapToSensorResponse)
                      .collect(Collectors.toList());

              log.debug(
                  "New client connected, sending initial state of {} sensors", currentState.size());

              return Flux.fromIterable(currentState).map(this::createServerSentEvent); // initial
            });

    Flux<ServerSentEvent<SensorResponse>> updates =
        sensorUpdatesSink.asFlux().map(this::createServerSentEvent); // update

    Flux<ServerSentEvent<SensorResponse>> heartbeats =
        Flux.interval(heartbeatInterval)
            .map(
                tick ->
                    ServerSentEvent.<SensorResponse>builder()
                        .id("heartbeat-" + System.currentTimeMillis())
                        .comment("heartbeat")
                        .build());

    return Flux.merge(initialState, updates, heartbeats)
        .onErrorResume(
            IOException.class,
            e -> {
              if (e.getMessage().contains("Broken pipe")
                  || e.getMessage().contains("Connection reset by peer")) {
                log.debug("Client disconnected from SSE stream (normal behavior)");
                return Mono.empty();
              }
              log.warn("IO error in SSE stream: {}", e.getMessage());
              return Mono.error(e);
            })
        .onErrorResume(
            e -> {
              log.error("Unhandled error in SSE stream: {}", e.getMessage());
              return Mono.empty();
            })
        .timeout(Duration.ofMinutes(15));
  }

  @Override
  public Flux<ServerSentEvent<SensorResponse>> getSensorUpdatesAsSSE(Short sensorId) {
    return getAllSensorsUpdatesAsSSE()
        .filter(
            sse -> {
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
