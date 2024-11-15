package skni.kamilG.skin_sensors_api.Service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skni.kamilG.skin_sensors_api.Exception.NoSensorsForFacultyException;
import skni.kamilG.skin_sensors_api.Exception.SensorAlreadyExistsException;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorRequest;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;

public interface ISensorService {

  // Main Functionalities
  SensorResponse getSensorById(Short sensorId);

  List<SensorData> getSensorDataById(
      Short sensorId, LocalDateTime startDate, LocalDateTime endDate);

  List<SensorResponse> getAllSensors();

  Page<SensorData> getAllSensorsData(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

  @SneakyThrows(NoSensorsForFacultyException.class)
  List<SensorResponse> getSensorsByFaculty(String facultyName);

  @SneakyThrows(NoSensorsForFacultyException.class)
  Page<SensorData> getSensorsDataByFaculty(
      String facultyName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

  // Admin Functionalities
  @SneakyThrows(SensorAlreadyExistsException.class)
  Sensor createSensor(SensorRequest sensor);

  SensorResponse updateSensor(SensorRequest sensor, Short sensorId);

  void deleteSensor(Short sensorId);
}
