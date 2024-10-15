package skni.kamilG.skin_sensors_api.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Exception.NoSensorDataFoundException;
import skni.kamilG.skin_sensors_api.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;

@Slf4j
@Service
public class SensorService implements ISensorService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;

  public SensorService(
      SensorRepository sensorRepository, SensorDataRepository sensorDataRepository) {
    this.sensorRepository = sensorRepository;
    this.sensorDataRepository = sensorDataRepository;
  }

  @Override
  public Sensor getSensorById(Short sensorId) {
    log.info("Rest getting sensor's info by id {}", sensorId);
    return sensorRepository
        .findById(sensorId)
        .orElseThrow(() -> new SensorNotFoundException(sensorId));
  }

  @Override
  public List<SensorData> getSensorDataById(
      Short sensorId, LocalDateTime startDate, LocalDateTime endDate) {
    log.info("Rest getting sensor's data by id {}", sensorId);
    return sensorDataRepository
        .findBySensor_SensorIdAndTimestampBetween(sensorId, startDate, endDate)
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  public List<Sensor> getAllSensors() {
    log.info("Rest getting all sensors' info");
    return sensorRepository.findAll();
  }

  @Override
  public Page<SensorData> getAllSensorsData(
      LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
    log.info("Rest getting all sensors' data in dateRange: {} -> {}", startDate, endDate);
    return sensorDataRepository
        .findByTimestampBetween(startDate, endDate, pageable)
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  public List<Sensor> getSensorsByFaculty(String facultyName) {
    log.info("Rest getting sensors from faculty {}", facultyName);
    return sensorRepository.findByLocationFacultyName(facultyName);
  }

  @Override
  public Page<SensorData> getSensorsDataByFaculty(
      String facultyName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
    log.info("Rest getting all sensors' from faculty {}", facultyName);
    List<Sensor> sensors = sensorRepository.findByLocationFacultyName(facultyName);
    return sensorDataRepository
        .findBySensorInAndTimestampBetween(sensors, startDate, endDate, pageable)
        .orElseThrow(() -> new NoSensorDataFoundException(startDate, endDate));
  }

  @Override
  public Sensor creatNewSensor(Sensor sensor) {
    log.info("Rest creating new sensor {}", sensor);
    return sensorRepository.save(sensor);
  }

  @Override
  public Sensor updateSensor(Sensor sensor) {
    // TODO przepisanie updatowania sensora + utworzenie changeStatus
    if (!sensorRepository.existsById(sensor.getSensorId())) {
      throw new SensorNotFoundException(sensor.getSensorId());
    }
    return sensorRepository.save(sensor);
  }

  @Override
  public void deleteSensorById(Short sensorId) {
    log.info("Rest deleting sensor by id {}", sensorId);
    if (!sensorRepository.existsById(sensorId)) {
      throw new SensorNotFoundException(sensorId);
    }
    sensorRepository.deleteById(sensorId);
  }
}
