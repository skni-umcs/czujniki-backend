package skni.kamilG.skin_sensors_api.LiveData;

import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/live-api/sensor")
public class LiveController {

  private final ISseEmitterService sseEmitterService;

  @GetMapping(value = "/{sensorId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamSensor(
      @PathVariable Short sensorId,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
      HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("X-Accel-Buffering", "no");
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Connection", "keep-alive");
    response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");

    String clientId = UUID.randomUUID().toString();
    log.info("New SSE connection request for client: {}, sensor: {}", clientId, sensorId);
    return sseEmitterService.createSseEmitter(clientId, sensorId);
  }

  @GetMapping(value = "/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamAllSensorsUpdates(
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
      HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("X-Accel-Buffering", "no");
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Connection", "keep-alive");
    response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");

    String clientId = UUID.randomUUID().toString();
    log.info("New SSE connection request for client: {}, all sensors", clientId);
    return sseEmitterService.createSseEmitter(clientId, null);
  }
}
