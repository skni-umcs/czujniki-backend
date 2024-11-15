package skni.kamilG.skin_sensors_api.Service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
import org.springframework.validation.annotation.Validated;
import skni.kamilG.skin_sensors_api.Exception.*;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorRequest;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.Mapper.SensorMapper;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@Validated
@AllArgsConstructor
public class SensorService implements ISensorService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;
  private final SensorMapper sensorMapper;

  @Override
  public SensorResponse getSensorById(@NotNull Short sensorId) {
    log.debug("Getting sensor by id: {}", sensorId);
    return sensorMapper.createSensorToSensorResponse(
        sensorRepository
            .findById(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(sensorId)));
  }

  @Override
  public List<SensorData> getSensorDataById(
      @NotNull Short sensorId,
      @NotNull @PastOrPresent LocalDateTime startDate,
      @NotNull @PastOrPresent LocalDateTime endDate) {
    validateDateRange(startDate, endDate);

    log.debug("Getting sensor data for id: {} between {} and {}", sensorId, startDate, endDate);

    return sensorDataRepository
        .findBySensorIdAndTimestampBetween(sensorId, startDate, endDate)
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
  public Page<SensorData> getAllSensorsData(
      @NotNull @PastOrPresent LocalDateTime startDate,
      @NotNull @PastOrPresent LocalDateTime endDate,
      @NotNull Pageable pageable) {
    validateDateRange(startDate, endDate);

    log.debug("Getting all sensors data between {} and {}, page: {}", startDate, endDate, pageable);

    return sensorDataRepository
        .findByTimestampBetween(startDate, endDate, pageable)
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  @SneakyThrows(NoSensorsForFacultyException.class)
  public List<SensorResponse> getSensorsByFaculty(@NotNull @NotBlank String facultyName) {
    log.debug("Getting sensors for faculty: {}", facultyName);
    List<Sensor> sensors = sensorRepository.findByLocationFacultyName(facultyName);

    if (sensors.isEmpty()) {
      throw new NoSensorsForFacultyException(facultyName);
    }
    return sensors.stream()
        .map(sensorMapper::createSensorToSensorResponse)
        .collect(Collectors.toList());
  }

  @Override
  public Page<SensorData> getSensorsDataByFaculty(
      @NotNull @NotBlank String facultyName,
      @NotNull @PastOrPresent LocalDateTime startDate,
      @NotNull @PastOrPresent LocalDateTime endDate,
      @NotNull Pageable pageable) {
    validateDateRange(startDate, endDate);

    log.debug(
        "Getting sensors data for faculty: {} between {} and {}", facultyName, startDate, endDate);

    List<Sensor> sensors = fetchSensorsByFaculty(facultyName);

    return sensorDataRepository
        .findBySensorInAndTimestampBetween(sensors, startDate, endDate, pageable)
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Transactional
  @Override
  public Sensor createSensor(@Validated SensorRequest createSensorRequestDTO) {
    log.debug("Creating new sensor");
    return sensorRepository.save(sensorMapper.createSensorRequestToSensor(createSensorRequestDTO));
  }

  @Transactional
  @Override
  public SensorResponse updateSensor(@Validated SensorRequest sensor, @NotNull Short id) {
    log.debug("Updating sensor with id: {}", id);

    Sensor existingSensor =
        sensorRepository.findById(id).orElseThrow(() -> new SensorNotFoundException(id));

    existingSensor.setLocation(sensor.location());
    existingSensor.setStatus(sensor.status());

    return sensorMapper.createSensorToSensorResponse(sensorRepository.save(existingSensor));
  }

  @Transactional
  @Override
  public void deleteSensor(@NotNull Short sensorId) {
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
  private List<Sensor> fetchSensorsByFaculty(@NotNull @NotBlank String facultyName) {
    log.debug("Internal getting sensors for faculty: {}", facultyName);
    List<Sensor> sensors = sensorRepository.findByLocationFacultyName(facultyName);

    if (sensors.isEmpty()) {
      throw new NoSensorsForFacultyException(facultyName);
    }
    return sensors;
  }
}
