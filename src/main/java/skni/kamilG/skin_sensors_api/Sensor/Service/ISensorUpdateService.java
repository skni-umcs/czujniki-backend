package skni.kamilG.skin_sensors_api.Sensor.Service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;

public interface ISensorUpdateService {

  Flux<ServerSentEvent<SensorResponse>> getSensorUpdatesAsSSE(Short sensorId);

  Flux<ServerSentEvent<SensorResponse>> getAllSensorsUpdatesAsSSE();
}
