package skni.kamilG.skin_sensors_api.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skni.kamilG.skin_sensors_api.Exception.*;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorDataResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorRequest;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.Mapper.SensorDataMapper;
import skni.kamilG.skin_sensors_api.Model.Sensor.Mapper.SensorMapper;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class SensorService implements ISensorService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;
  private final SensorMapper sensorMapper;
  private final SensorDataMapper sensorDataMapper;

  @Override
  public SensorResponse getSensorById(Short sensorId) {
    log.debug("Getting sensor by id: {}", sensorId);
    return sensorMapper.createSensorToSensorResponse(
        sensorRepository
            .findById(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(sensorId)));
  }

  @Override
  public List<SensorDataResponse> getSensorDataById(
      Short sensorId, LocalDateTime startDate, LocalDateTime endDate) {
    validateDateRange(startDate, endDate);

    log.debug("Getting sensor data for id: {} between {} and {}", sensorId, startDate, endDate);

    return sensorDataRepository
        .findBySensorIdAndTimestampBetween(sensorId, startDate, endDate)
        .map(
            data ->
                data.stream()
                    .map(sensorDataMapper::createSensorDataToSensorDataResponse)
                    .collect(Collectors.toList()))
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  public List<SensorResponse> getAllSensors() {
    log.debug("Getting all sensors");
    return sensorRepository.findAll().stream()
        .map(sensorMapper::createSensorToSensorResponse)
        .collect(Collectors.toList());
  }

  @Override
  public Page<SensorDataResponse> getAllSensorsData(
      LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
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
    return sensors.stream()
        .map(sensorMapper::createSensorToSensorResponse)
        .collect(Collectors.toList());
  }

  @Override
  public Page<SensorDataResponse> getSensorsDataByFaculty(
      String facultyName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
    validateDateRange(startDate, endDate);

    log.debug(
        "Getting sensors data for faculty: {} between {} and {}", facultyName, startDate, endDate);

    List<Sensor> sensors = fetchSensorsByFaculty(facultyName);

    return sensorDataRepository
        .findBySensorInAndTimestampBetween(sensors, startDate, endDate, pageable)
        .map(page -> page.map(sensorDataMapper::createSensorDataToSensorDataResponse))
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Transactional
  @Override
  public Sensor createSensor(SensorRequest createSensorRequestDTO) {
    log.debug("Creating new sensor");
    return sensorRepository.save(sensorMapper.createSensorRequestToSensor(createSensorRequestDTO));
  }

  @Transactional
  @Override
  public SensorResponse updateSensor(SensorRequest sensor, Short id) {
    log.debug("Updating sensor with id: {}", id);

    Sensor existingSensor =
        sensorRepository.findById(id).orElseThrow(() -> new SensorNotFoundException(id));

    existingSensor.setLocation(sensor.location());
    existingSensor.setStatus(sensor.status());

    return sensorMapper.createSensorToSensorResponse(sensorRepository.save(existingSensor));
  }

  @Override
  public void deleteSensor(Short sensorId) {
    log.info("Deleting sensor with id: {}", sensorId);
    getSensorById(sensorId);
    sensorRepository.deleteById(sensorId);
  }

  private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
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
