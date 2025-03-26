package skni.kamilG.skin_sensors_api.Sensor.Service;

import java.util.List;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;

public interface ISensorUpdateService {
  List<Sensor> findSensorsToUpdate();

  void updateSingleSensor(Sensor sensor);
}
