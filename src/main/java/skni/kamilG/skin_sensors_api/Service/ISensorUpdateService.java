package skni.kamilG.skin_sensors_api.Service;

import lombok.SneakyThrows;
import skni.kamilG.skin_sensors_api.Exception.SensorUpdateException;

public interface ISensorUpdateService {
  void updateSensorsData();

  void forceUpdateSensorsData();

  @SneakyThrows(SensorUpdateException.class)
  void performSensorDataUpdate();
}
