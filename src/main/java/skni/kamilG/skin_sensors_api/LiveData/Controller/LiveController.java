package skni.kamilG.skin_sensors_api.LiveData.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import skni.kamilG.skin_sensors_api.LiveData.Service.ISseEmitterService;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/live-api/sensors")
public class LiveController {

    private final ISseEmitterService sseEmitterService;

    @GetMapping(value = "/{sensorId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> streamSensor(
            @PathVariable Short sensorId,
            @SuppressWarnings("unused") @RequestHeader(value = "Last-Event-ID", required = false)
            String lastEventId) {

        String clientId = UUID.randomUUID().toString();
        log.debug("New SSE connection request for client: {}, sensor: {}", clientId, sensorId);
        SseEmitter emitter = sseEmitterService.createSseEmitter(clientId, sensorId);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("Cache-Control", "no-store")
                .header("X-Accel-Buffering", "no")
                .header("Connection", "keep-alive")
                .body(emitter);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> streamAllSensorsUpdates(
            @SuppressWarnings("unused") @RequestHeader(value = "Last-Event-ID", required = false)
            String lastEventId) {
        String clientId = UUID.randomUUID().toString();
        log.info("New SSE connection request for client: {}, all sensors", clientId);
        SseEmitter emitter = sseEmitterService.createSseEmitter(clientId, null);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("Cache-Control", "no-store")
                .header("X-Accel-Buffering", "no")
                .header("Connection", "keep-alive")
                .body(emitter);
    }
}
