package skni.kamilG.skin_sensors_api.Sensor.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import skni.kamilG.skin_sensors_api.Influx.IInfluxService;
import skni.kamilG.skin_sensors_api.Sensor.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.Mapper.SensorMapper;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorData;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorRepository;

/**
 * Executes aync process of updating all sensors current data. @See ISensorUpdateService for
 * definition of the methods
 */
@Slf4j
@Service
@AllArgsConstructor
public class SensorUpdateService implements ISensorUpdateService {

  private final SensorRepository sensorRepository;
  private final Sinks.Many<SensorResponse> sensorUpdatesSink;
  private final SensorMapper sensorMapper;
  private final Duration heartbeatInterval;
  private final IInfluxService influxService;

  public void updateSingleSensor(Short sensorId, Short refreshRate) {
    Optional<SensorData> currentReading = influxService.fetchLatestData(sensorId, refreshRate);
    if (!currentReading.isPresent()) {
      Sensor sensorToUpdate =
          sensorRepository
              .findById(sensorId)
              .orElseThrow(() -> new SensorNotFoundException(sensorId));

      sensorToUpdate.set
    }
    //TODO trackowanie bledow z sensorUdpate Filaurte
  }

  @Override
  public Flux<ServerSentEvent<SensorResponse>> getAllSensorsUpdatesAsSSE() {
    Flux<ServerSentEvent<SensorResponse>> initialState =
        Flux.defer(
            () -> {
              List<SensorResponse> currentState =
                  sensorRepository.findAll().stream()
                      .map(sensorMapper::createSensorToSensorResponse)
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
