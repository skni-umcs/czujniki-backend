package skni.kamilG.skin_sensors_api.LiveData.Service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;

public interface ISseEmitterService {
  SseEmitter createSseEmitter(String clientId, Short sensorId);

  void broadcastSensorUpdate(SensorResponse sensorResponse);

  void removeEmitter(String clientId);

  void sendHeartbeat();

  int getActiveConnectionCount();
}
