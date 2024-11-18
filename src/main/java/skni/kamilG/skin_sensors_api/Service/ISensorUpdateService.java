package skni.kamilG.skin_sensors_api.Service;

import java.util.List;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import skni.kamilG.skin_sensors_api.Exception.SensorUpdateException;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;

public interface ISensorUpdateService {
  void updateSensorsData();

  void forceUpdateSensorsData();

  @SneakyThrows(SensorUpdateException.class)
  List<SensorResponse> performSensorDataUpdate();

  Flux<SensorResponse> getAllSensorsUpdates();

  Flux<SensorResponse> getSensorUpdates(Short sensorId);
}
