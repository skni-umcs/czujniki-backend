package skni.kamilG.skin_sensors_api.LiveData;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/sensor")
public class LiveController {

  private final ISseEmitterService sseEmitterService;

  @GetMapping(value = "/{sensorId}/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamSingleSensor(@PathVariable Short sensorId) {
    String clientId = UUID.randomUUID().toString();
    log.debug("New client {} connected to sensor {}", clientId, sensorId);
    return sseEmitterService.createSseEmitter(clientId, sensorId);
  }

  @GetMapping(value = "/all/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamAllSensorsUpdates() {
    String clientId = UUID.randomUUID().toString();
    log.debug("New client {} connected to all sensors", clientId);
    return sseEmitterService.createSseEmitter(clientId, null);
  }
}
