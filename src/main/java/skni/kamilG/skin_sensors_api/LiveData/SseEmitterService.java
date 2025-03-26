package skni.kamilG.skin_sensors_api.LiveData;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
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
  private final Clock clock;

  @Value("${see.timeout.limit-minutes:5}")
  private int sseConnectionLimit;

  @Override
  public SseEmitter createSseEmitter(String clientId, Short sensorId) {
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
          log.warn("SEE error for client {}: {}", clientId, e.getMessage());
          removeEmitter(clientId);
        });
    emitters.put(clientId, emitter);
    if (sensorId != null) {
      sensorSubscriptions.computeIfAbsent(sensorId, k -> new CopyOnWriteArraySet<>()).add(clientId);
      try {
        emitter.send(
            SseEmitter.event().id("heartbeat-connect").name("heartbeat").data("connected"));

        sendInitialState(sensorId);
        log.debug("SSE connected for client: {}", clientId);
      } catch (IOException e) {
        log.warn("SSE connect error for client: {}", clientId);
        removeEmitter(clientId);
      }
    } else {
      allSensorsSubscribers.add(clientId);
      log.debug("Client: {} subscribed to all sensors", clientId);
    }

    return emitter;
  }

  @Override
  public void broadcastSensorUpdate(SensorResponse sensorResponse) {
    SseEmitter.SseEventBuilder event =
        SseEmitter.event()
            .id(sensorResponse.id() + "-" + ZonedDateTime.now(clock))
            .name("sensor-update")
            .data(sensorResponse);
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
                  "Nie udało się wysłać aktualizacji do klienta {}, usuwanie: {}",
                  clientId,
                  e.getMessage());
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
            .id("heartbeat-" + ZonedDateTime.now(clock))
            .name("heartbeat")
            .data("ping");

    emitters.forEach(
        (clientId, emitter) -> {
          try {
            emitter.send(heartbeat);
          } catch (IOException e) {
            log.debug(
                "Error sending heartbeat to client {}, deleting: {}", clientId, e.getMessage());
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
    Thread.startVirtualThread(
        () -> {
          try {
            Thread.sleep(100);
            if (sensorId != null) {
              broadcastSensorUpdate(sensorService.getSensorById(sensorId));
            } else {
              sensorService.getAllSensors().forEach(this::broadcastSensorUpdate);
            }
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
