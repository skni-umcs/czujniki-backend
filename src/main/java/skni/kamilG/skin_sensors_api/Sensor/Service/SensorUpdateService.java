package skni.kamilG.skin_sensors_api.Sensor.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
 * Executes async process of updating all sensors current data.
 *
 * @see ISensorUpdateService for definition of the methods
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

  @Value("${see.timeout.limit-minutes:5}")
  private int timeoutConnectionLimit;

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
    } else {
      recordUpdateFailure(sensor);
    }
    sensorUpdatesSink.tryEmitNext(sensorMapper.mapToSensorResponse(sensor));
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
        new SensorUpdateFailure(ZonedDateTime.now(clock), warning, sensor));
    sensorRepository.save(sensor);
  }

  private void recoverSensorFromError(Sensor sensor) {
    sensor.setStatus(SensorStatus.ONLINE);
    SensorUpdateFailure failureToChange =
        sensorUpdateFailureRepository.getBySensorIdAndResolvedTimeIsNull(sensor.getId());
    failureToChange.setResolvedTime(ZonedDateTime.now(clock));
    sensorUpdateFailureRepository.save(failureToChange);
    log.info("Sensor {} is back online", sensor.getId());
  }

  /**
   * Creates a heartbeat flux that emits keepalive events at regular intervals. The heartbeat will
   * automatically stop when the source flux completes or errors.
   *
   * @param interval The time interval between heartbeats
   * @param sourceFlux The source flux to "monitor" for completion/errors
   * @return A flux of heartbeat ServerSentEvents
   */
  private Flux<ServerSentEvent<SensorResponse>> createHeartbeatFlux(
      Duration interval, Flux<?> sourceFlux) {

    AtomicBoolean isCancelled = new AtomicBoolean(false);

    return Flux.interval(interval)
        .takeUntilOther(
            sourceFlux
                .materialize()
                .filter(signal -> signal.isOnComplete() || signal.isOnError())
                .doOnNext(signal -> isCancelled.set(true))
                .then(Mono.never()))
        .takeWhile(tick -> !isCancelled.get())
        .map(
            tick -> {
              ZonedDateTime now = ZonedDateTime.now(clock);
              String formattedTime = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
              return ServerSentEvent.<SensorResponse>builder()
                  .id("heartbeat-" + formattedTime)
                  .comment("heartbeat")
                  .build();
            })
        .doOnSubscribe(s -> log.debug("Heartbeat started"))
        .doFinally(signal -> log.debug("Heartbeat stopped: {}", signal));
  }

  @Override
  public Flux<ServerSentEvent<SensorResponse>> getAllSensorsUpdatesAsSSE() {
    Flux<Long> ttl = Mono.delay(Duration.ofMinutes(timeoutConnectionLimit)).flux();

    Flux<ServerSentEvent<SensorResponse>> initialState =
        Flux.defer(
            () -> {
              List<SensorResponse> currentState =
                  sensorRepository.findAll().stream()
                      .map(sensorMapper::mapToSensorResponse)
                      .collect(Collectors.toList());

              log.debug(
                  "New client connected, sending initial state of {} sensors", currentState.size());
              return Flux.fromIterable(currentState).map(this::createServerSentEvent);
            });

    Flux<ServerSentEvent<SensorResponse>> updates =
        sensorUpdatesSink
            .asFlux()
            .map(this::createServerSentEvent)
            .doOnError(e -> log.error("Error in sensor updates sink: {}", e.getMessage()));

    Flux<ServerSentEvent<SensorResponse>> dataFlux = Flux.merge(initialState, updates);

    Flux<ServerSentEvent<SensorResponse>> heartbeats =
        createHeartbeatFlux(heartbeatInterval, dataFlux);

    return Flux.merge(dataFlux, heartbeats)
        .onErrorResume(
            IOException.class,
            e -> {
              if (isClientDisconnectError(e)) {
                log.debug(
                    "Client disconnected from SSE stream (normal behavior): {}", e.getMessage());
                return Mono.empty();
              }
              log.warn("IO error in SSE stream: {}", e.getMessage());
              return Mono.empty();
            })
        .onErrorResume(
            e -> {
              log.error("Unhandled error in SSE stream: {}", e.getMessage(), e);
              return Mono.empty();
            })
        .takeUntilOther(ttl)
        .doOnCancel(() -> log.debug("SSE stream cancelled"))
        .doOnComplete(() -> log.debug("SSE stream completed"))
        .doOnTerminate(() -> log.debug("SSE stream terminated"));
  }

  /**
   * Checks if an IOException is caused by a client disconnection.
   *
   * @param e The IOException to check
   * @return true if the exception is likely caused by client disconnection
   */
  private boolean isClientDisconnectError(IOException e) {
    String message = e.getMessage();
    if (message == null) {
      return false;
    }

    return message.contains("Broken pipe")
        || message.contains("Connection reset by peer")
        || message.contains("An established connection was aborted")
        || message.contains("An existing connection was forcibly closed")
        || message.contains("socket write error");
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
    ZonedDateTime now = ZonedDateTime.now(clock);
    String formattedTime = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    ServerSentEvent<SensorResponse> event =
        ServerSentEvent.<SensorResponse>builder()
            .id(sensor.id() + "-" + formattedTime)
            .event("sensor-update")
            .data(sensor)
            .build();

    log.debug("Created SSE with id: {}", event.id());
    return event;
  }
}
