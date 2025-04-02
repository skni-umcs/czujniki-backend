package skni.kamilG.skin_sensors_api.Sensor.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Sensor.Exception.InvalidDateRangeException;
import skni.kamilG.skin_sensors_api.Sensor.Exception.NoSensorDataFoundException;
import skni.kamilG.skin_sensors_api.Sensor.Exception.NoSensorsForFacultyException;
import skni.kamilG.skin_sensors_api.Sensor.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorDataResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.Mapper.SensorDataMapper;
import skni.kamilG.skin_sensors_api.Sensor.Model.Mapper.SensorMapper;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorRepository;

@Slf4j
@Service
@AllArgsConstructor
public class SensorService implements ISensorService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;
  private final SensorMapper sensorMapper;
  private final SensorDataMapper sensorDataMapper;

  @Override
  public SensorResponse getSensorById(Short sensorId) {
    log.debug("Getting sensor by id: {}", sensorId);
    return sensorMapper.mapToSensorResponse(
        sensorRepository
            .findById(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(sensorId)));
  }

  @Override
  public Page<SensorDataResponse> getSensorDataById(
      Short sensorId, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable) {
    validateDateRange(startDate, endDate);

    log.debug(
        "Getting paginated sensor data for id: {} between {} and {}, page: {}",
        sensorId,
        startDate,
        endDate,
        pageable);
    return sensorDataRepository
        .findBySensorIdAndTimestampBetween(sensorId, startDate, endDate, pageable)
        .map(page -> page.map(sensorDataMapper::createSensorDataToSensorDataResponse))
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  public Page<SensorResponse> findAllWithCustomSorting(Pageable pageable) {
    log.debug("Getting all sensors with pagination: {}", pageable);
    Page<Sensor> sensorPage = sensorRepository.findAllWithCustomSorting(pageable);
    return sensorPage.map(sensorMapper::mapToSensorResponse);
  }

  @Override
  public List<SensorResponse> getAllSensors() {
    log.debug("Getting all sensors");
    return sensorRepository.findAll().stream()
        .map(sensorMapper::mapToSensorResponse)
        .collect(Collectors.toList());
  }

  @Override
  public Page<SensorDataResponse> getAllSensorsData(
      ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable) {
    validateDateRange(startDate, endDate);

    log.debug("Getting all sensors data between {} and {}, page: {}", startDate, endDate, pageable);

    return sensorDataRepository
        .findByTimestampBetween(startDate, endDate, pageable)
        .map(page -> page.map(sensorDataMapper::createSensorDataToSensorDataResponse))
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  @SneakyThrows(NoSensorsForFacultyException.class)
  public List<SensorResponse> getSensorsByFaculty(String facultyName) {
    log.debug("Getting sensors for faculty: {}", facultyName);
    List<Sensor> sensors = sensorRepository.findByLocationFacultyAbbreviation(facultyName);

    if (sensors.isEmpty()) {
      throw new NoSensorsForFacultyException(facultyName);
    }
    return sensors.stream().map(sensorMapper::mapToSensorResponse).collect(Collectors.toList());
  }

  @Override
  public Page<SensorDataResponse> getSensorsDataByFaculty(
      String facultyName, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable) {
    log.debug(
        "Getting sensors data for faculty: {} between {} and {}", facultyName, startDate, endDate);

    List<Sensor> sensors = fetchSensorsByFaculty(facultyName);

    return sensorDataRepository
        .findBySensorInAndTimestampBetween(sensors, startDate, endDate, pageable)
        .map(page -> page.map(sensorDataMapper::createSensorDataToSensorDataResponse))
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  public Page<SensorResponse> searchSensors(String searchTerm, Pageable pageable) {
    return sensorRepository
        .searchSensors(searchTerm, pageable)
        .map(sensorMapper::mapToSensorResponse);
  }

  private void validateDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
    if (startDate.isAfter(endDate)) {
      throw new InvalidDateRangeException("Start date must be before or equal to end date");
    }
  }

  @SneakyThrows(NoSensorsForFacultyException.class)
  private List<Sensor> fetchSensorsByFaculty(String facultyName) {
    log.debug("Internal getting sensors for faculty: {}", facultyName);
    List<Sensor> sensors = sensorRepository.findByLocationFacultyAbbreviation(facultyName);

    if (sensors.isEmpty()) {
      throw new NoSensorsForFacultyException(facultyName);
    }
    return sensors;
  }
}
