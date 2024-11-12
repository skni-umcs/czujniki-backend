package skni.kamilG.skin_sensors_api.Service;

import java.util.List;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import skni.kamilG.skin_sensors_api.Exception.SensorUpdateException;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;

public interface ISensorUpdateService {
  void updateSensorsData();

  void forceUpdateSensorsData();

  @SneakyThrows(SensorUpdateException.class)
  List<Sensor> performSensorDataUpdate();

  Flux<Sensor> getAllSensorsUpdates();

  Flux<Sensor> getSensorUpdates(Short sensorId);
}
