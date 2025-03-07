package skni.kamilG.skin_sensors_api.Sensor.Service;

import java.util.List;
import lombok.SneakyThrows;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import skni.kamilG.skin_sensors_api.Sensor.Exception.SensorUpdateException;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;

public interface ISensorUpdateService {
  void updateSensorsData();

  void forceUpdateSensorsData();

  @SneakyThrows(SensorUpdateException.class)
  List<SensorResponse> performSensorDataUpdate();

  Flux<ServerSentEvent<SensorResponse>> getSensorUpdatesAsSSE(Short sensorId);

  Flux<ServerSentEvent<SensorResponse>> getAllSensorsUpdatesAsSSE();
}
