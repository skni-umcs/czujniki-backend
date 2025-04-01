package skni.kamilG.skin_sensors_api.LiveData;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import skni.kamilG.skin_sensors_api.Sensor.Exception.SseConnectionException;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Sensor.Service.ISensorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService implements ISseEmitterService {

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final Map<Short, Set<String>> sensorSubscriptions = new ConcurrentHashMap<>();
  private final Set<String> allSensorsSubscribers = new CopyOnWriteArraySet<>();
  private final ISensorService sensorService;

  @Value("${see.timeout.limit-minutes:5}")
  private int sseConnectionLimit;

  @Override
  public SseEmitter createSseEmitter(String clientId, Short sensorId) {
    SseEmitter emitter = createEmitterWithConnectionErrorHandlers(clientId);
    try {
      sendConnectionConfig(emitter);
      emitters.put(clientId, emitter);
      subscribeClientToSensors(clientId, sensorId);
    } catch (IOException e) {
      log.warn("SSE connect error for client: {}", clientId);
      removeEmitter(clientId);
      throw new SseConnectionException(e);
    }
    return emitter;
  }

  @Override
  public void broadcastSensorUpdate(SensorResponse sensorResponse) {
    SseEmitter.SseEventBuilder event =
        SseEmitter.event()
            .id("update-" + sensorResponse.id() + "-" + System.currentTimeMillis())
            .name("sensor-update")
            .data(sensorResponse)
            .reconnectTime(3000);

    Set<String> sensorSubscribers = sensorSubscriptions.getOrDefault(sensorResponse.id(), Set.of());

    emitters.forEach(
        (clientId, emitter) -> {
          boolean isSubscribedToAll = allSensorsSubscribers.contains(clientId);
          boolean isSubscribedToThisSensor = sensorSubscribers.contains(clientId);
          if (isSubscribedToAll || isSubscribedToThisSensor) {
            try {
              emitter.send(event);
            } catch (IOException e) {
              log.debug(
                  "Failed to send update to client {}, removing: {}", clientId, e.getMessage());
              removeEmitter(clientId);
            }
          }
        });
  }

  @Override
  public void removeEmitter(String clientId) {
    if (emitters.remove(clientId) != null) {
      allSensorsSubscribers.remove(clientId);
      sensorSubscriptions.values().forEach(clients -> clients.remove(clientId));
      sensorSubscriptions.entrySet().removeIf(entry -> entry.getValue().isEmpty());
      log.debug("Removed SSE emitter for client: {}", clientId);
    }
  }

  @Override
  public void sendHeartbeat() {
    SseEmitter.SseEventBuilder heartbeat =
        SseEmitter.event()
            .id("heartbeat-" + System.currentTimeMillis())
            .name("heartbeat")
            .data("ping")
            .reconnectTime(3000);

    emitters.forEach(
        (clientId, emitter) -> {
          try {
            emitter.send(heartbeat);
          } catch (IOException e) {
            log.debug(
                "Error sending heartbeat to client {}, removing: {}", clientId, e.getMessage());
            removeEmitter(clientId);
          }
        });

    log.debug("Sent heartbeat to {} connected clients", emitters.size());
  }

  @Override
  public int getActiveConnectionCount() {
    return emitters.size();
  }

  private void sendInitialState(Short sensorId) {
    try {
      SensorResponse sensor = sensorService.getSensorById(sensorId);
      SseEmitter.SseEventBuilder event =
          SseEmitter.event()
              .id("initial-" + sensorId + "-" + System.currentTimeMillis())
              .name("sensor-update")
              .data(sensor)
              .reconnectTime(1000);

      emitters.forEach(
          (clientId, emitter) -> {
            Set<String> subscribers = sensorSubscriptions.getOrDefault(sensorId, Set.of());
            if (subscribers.contains(clientId)) {
              try {
                emitter.send(event);
                log.debug("Sent initial state for sensor {} to client {}", sensorId, clientId);
              } catch (IOException e) {
                log.warn("Failed to send initial state to client {}: {}", clientId, e.getMessage());
                removeEmitter(clientId);
              }
            }
          });
    } catch (Exception e) {
      log.error("Error sending initial state: {}", e.getMessage());
    }
  }

  private void sendInitialStatesForAllSensors() {
    List<SensorResponse> sensors = sensorService.getAllSensors();
    for (SensorResponse sensor : sensors) {
      try {
        SseEmitter.SseEventBuilder event =
            SseEmitter.event()
                .id("initial-" + sensor.id() + "-" + System.currentTimeMillis())
                .name("sensor-update")
                .data(sensor)
                .reconnectTime(1000);

        emitters.forEach(
            (clientId, emitter) -> {
              if (allSensorsSubscribers.contains(clientId)) {
                try {
                  emitter.send(event);
                  log.debug("Sent initial state of all sensors to client: {}", clientId);
                } catch (IOException e) {
                  log.warn(
                      "Failed to send initial state of all sensors to client {}: {}",
                      clientId,
                      e.getMessage());
                  removeEmitter(clientId);
                }
              }
            });
      } catch (Exception e) {
        log.error("Error sending initial state of all sensors: {}", e.getMessage());
      }
    }
  }

  private SseEmitter createEmitterWithConnectionErrorHandlers(String clientId) {
    SseEmitter emitter = new SseEmitter(Duration.ofMinutes(sseConnectionLimit).toMillis());
    emitter.onCompletion(
        () -> {
          log.debug("SSE completion for client: {}", clientId);
          removeEmitter(clientId);
        });
    emitter.onTimeout(
        () -> {
          log.debug("SSE timeout for client: {}", clientId);
          removeEmitter(clientId);
        });
    emitter.onError(
        e -> {
          log.warn("SSE error for client {}: {}", clientId, e.getMessage());
          removeEmitter(clientId);
        });
    return emitter;
  }

  private void sendConnectionConfig(SseEmitter emitter) throws IOException {
    emitter.send(
        SseEmitter.event()
            .id("connect-" + System.currentTimeMillis())
            .name("connect")
            .data("connected")
            .reconnectTime(1000));
  }

  private void subscribeClientToSensors(String clientId, Short sensorId) {
    if (sensorId != null) {
      sensorSubscriptions.computeIfAbsent(sensorId, k -> new CopyOnWriteArraySet<>()).add(clientId);
      sendInitialState(sensorId);
    } else {
      allSensorsSubscribers.add(clientId);
      sendInitialStatesForAllSensors();
    }
    log.debug("SSE connected for client: {}", clientId);
  }
}
