package skni.kamilG.skin_sensors_api.Service;

import java.util.List;
import lombok.SneakyThrows;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import skni.kamilG.skin_sensors_api.Exception.SensorUpdateException;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;

public interface ISensorUpdateService {
  void updateSensorsData();

  void forceUpdateSensorsData();

  @SneakyThrows(SensorUpdateException.class)
  List<SensorResponse> performSensorDataUpdate();

  Flux<ServerSentEvent<SensorResponse>> getSensorUpdatesAsSSE(Short sensorId);

  Flux<ServerSentEvent<SensorResponse>> getAllSensorsUpdatesAsSSE();
}
