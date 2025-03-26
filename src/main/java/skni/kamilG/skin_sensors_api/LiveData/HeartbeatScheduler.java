package skni.kamilG.skin_sensors_api.LiveData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartbeatScheduler {

  private final ISseEmitterService sseEmitterService;

  /**
   * Sends heartbeats to all connected SSE clients at a regular interval. The default rate is 25
   * seconds, which is typically short enough to keep connections from timing out due to inactivity.
   */
  @Scheduled(fixedRateString = "#{heartbeatInterval.toMillis()}")
  public void sendPeriodicHeartbeat() {
    int clientCount = sseEmitterService.getActiveConnectionCount();

    if (clientCount > 0) {
      log.debug("Sending scheduled heartbeat to {} connected clients", clientCount);
      sseEmitterService.sendHeartbeat();
    }
  }
}
