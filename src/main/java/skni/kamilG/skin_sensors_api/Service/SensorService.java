package skni.kamilG.skin_sensors_api.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import skni.kamilG.skin_sensors_api.Exception.*;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;

//TODO weryfikacja pelnej logiki
@Slf4j
@Service
@Transactional(readOnly = true)
@Validated
public class SensorService implements ISensorService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;

  public SensorService(
          SensorRepository sensorRepository,
          SensorDataRepository sensorDataRepository) {
    this.sensorRepository = sensorRepository;
    this.sensorDataRepository = sensorDataRepository;
  }

  @Override
  public Sensor getSensorById(@NotNull Short sensorId) {
    log.debug("Getting sensor by id: {}", sensorId);
    return sensorRepository.findById(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(sensorId));
  }

  @Override
  public List<SensorData> getSensorDataById(
          @NotNull Short sensorId,
          @NotNull @PastOrPresent LocalDateTime startDate,
          @NotNull @PastOrPresent LocalDateTime endDate) {
    validateDateRange(startDate, endDate);

    log.debug("Getting sensor data for id: {} between {} and {}",
            sensorId, startDate, endDate);

    return sensorDataRepository
            .findBySensor_SensorIdAndTimestampBetween(sensorId, startDate, endDate)
            .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  public List<Sensor> getAllSensors() {
    log.debug("Getting all sensors");
    return sensorRepository.findAll();
  }

  @Override
  public Page<SensorData> getAllSensorsData(
          @NotNull @PastOrPresent LocalDateTime startDate,
          @NotNull @PastOrPresent LocalDateTime endDate,
          @NotNull Pageable pageable) {
    validateDateRange(startDate, endDate);

    log.debug("Getting all sensors data between {} and {}, page: {}",
            startDate, endDate, pageable);

    return sensorDataRepository
            .findByTimestampBetween(startDate, endDate, pageable)
            .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  public List<Sensor> getSensorsByFaculty(@NotNull @NotBlank String facultyName) throws NoSensorsForFacultyException {
    log.debug("Getting sensors for faculty: {}", facultyName);
    List<Sensor> sensors = sensorRepository.findByLocationFacultyName(facultyName);

    if (sensors.isEmpty()) {
      throw new NoSensorsForFacultyException(facultyName);
    }

    return sensors;
  }

  @Override
  public Page<SensorData> getSensorsDataByFaculty(
          @NotNull @NotBlank String facultyName,
          @NotNull @PastOrPresent LocalDateTime startDate,
          @NotNull @PastOrPresent LocalDateTime endDate,
          @NotNull Pageable pageable) throws NoSensorsForFacultyException {
    validateDateRange(startDate, endDate);

    log.debug("Getting sensors data for faculty: {} between {} and {}",
            facultyName, startDate, endDate);

    List<Sensor> sensors = getSensorsByFaculty(facultyName);

    return sensorDataRepository
            .findBySensorInAndTimestampBetween(sensors, startDate, endDate, pageable)
            .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Transactional
  @Override
  public Sensor createSensor(@Valid @NotNull Sensor sensor) throws SensorAlreadyExistsException {
    log.debug("Creating new sensor");
    validateSensorForCreation(sensor);
    return sensorRepository.save(sensor);
  }

  @Transactional
  @Override
  public Sensor updateSensor(@Valid @NotNull Sensor sensor) {
    log.debug("Updating sensor with id: {}", sensor.getSensorId());

    Sensor existingSensor = getSensorById(sensor.getSensorId());

    existingSensor.setLocation(sensor.getLocation());
    existingSensor.setStatus(sensor.getStatus());

    return sensorRepository.save(existingSensor);
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

  private void validateSensorForCreation(Sensor sensor) throws SensorAlreadyExistsException {
    if (sensor.getSensorId() != null &&
            sensorRepository.existsById(sensor.getSensorId())) {
      throw new SensorAlreadyExistsException(sensor.getSensorId());
    }
  }
}
