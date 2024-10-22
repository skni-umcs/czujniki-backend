package skni.kamilG.skin_sensors_api.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import skni.kamilG.skin_sensors_api.Exception.NoSensorsForFacultyException;
import skni.kamilG.skin_sensors_api.Exception.SensorAlreadyExistsException;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;

public interface ISensorService {

  // Main Functionalities
  Sensor getSensorById(Short sensorId);

  List<SensorData> getSensorDataById(
      Short sensorId, LocalDateTime startDate, LocalDateTime endDate);

  List<Sensor> getAllSensors();

  Page<SensorData> getAllSensorsData(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

  List<Sensor> getSensorsByFaculty(String facultyName) throws NoSensorsForFacultyException;

  Page<SensorData> getSensorsDataByFaculty(
      String facultyName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) throws NoSensorsForFacultyException;

  // Admin Functionalities
  Sensor createSensor(Sensor sensor) throws SensorAlreadyExistsException;

  Sensor updateSensor(Sensor sensor);

  void deleteSensor(Short sensorId);
}
