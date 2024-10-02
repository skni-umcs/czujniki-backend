package skni.kamilG.skin_sensors_api.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;

public interface ISensorService {

  // Main Functionalities
  Sensor getSensorById(Short sensorId);

  Optional<List<SensorData>> getSensorDataById(
      Short sensorId, LocalDateTime startDate, LocalDateTime endDate);

  List<Sensor> getAllSensors();

  Optional<List<SensorData>> getAllSensorsData(LocalDateTime startDate, LocalDateTime endDate);

  List<Sensor> getSensorsByFaculty(String facultyName);

  Optional<List<SensorData>> getSensorsDataByFaculty(
      String facultyName, LocalDateTime startDate, LocalDateTime endDate);

  // Admin Functionalities
  Sensor addSensor(Sensor sensor);

  Sensor updateSensor(Sensor sensor);

  void deleteSensor(Short sensorId);
}
