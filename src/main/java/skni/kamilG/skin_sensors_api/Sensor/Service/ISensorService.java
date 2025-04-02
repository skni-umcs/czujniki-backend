package skni.kamilG.skin_sensors_api.Sensor.Service;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skni.kamilG.skin_sensors_api.Sensor.Exception.NoSensorsForFacultyException;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorDataResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;

public interface ISensorService {

  // Main Functionalities
  SensorResponse getSensorById(Short sensorId);

  Page<SensorDataResponse> getSensorDataById(
      Short sensorId, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable);

  Page<SensorResponse> findAllWithCustomSorting(Pageable pageable);

  List<SensorResponse> getAllSensors();

  Page<SensorDataResponse> getAllSensorsData(
      ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable);

  @SneakyThrows(NoSensorsForFacultyException.class)
  List<SensorResponse> getSensorsByFaculty(String facultyName);

  @SneakyThrows(NoSensorsForFacultyException.class)
  Page<SensorDataResponse> getSensorsDataByFaculty(
      String facultyName, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable);

  Page<SensorResponse> searchSensors(String searchTerm, Pageable pageable);
}
