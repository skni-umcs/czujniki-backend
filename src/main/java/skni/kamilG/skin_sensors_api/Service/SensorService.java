package skni.kamilG.skin_sensors_api.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;

@Service
public class SensorService implements ISensorService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;

  @Autowired
  public SensorService(
      SensorRepository sensorRepository, SensorDataRepository sensorDataRepository) {
    this.sensorRepository = sensorRepository;
    this.sensorDataRepository = sensorDataRepository;
  }

  @Override
  public Sensor getSensorById(Short sensorId) {
    return sensorRepository
        .findById(sensorId)
        .orElseThrow(() -> new SensorNotFoundException(sensorId));
  }

  @Override
  public List<SensorData> getSensorHistoryById(
      Short sensorId, LocalDateTime startDate, LocalDateTime endDate) {
    return sensorDataRepository.findBySensor_SensorIdAndTimestampBetween(
        sensorId, startDate, endDate);
  }

  @Override
  public List<Sensor> getAllSensorsData() {
    return sensorRepository.findAll();
  }

  @Override
  public List<SensorData> getAllSensorsHistory(LocalDateTime startDate, LocalDateTime endDate) {
    return sensorDataRepository.findByTimestampBetween(startDate, endDate);
  }

  @Override
  public List<Sensor> getSensorsByFaculty(String facultyName) {
    return sensorRepository.findByLocationFacultyName(facultyName);
  }

  @Override
  public List<SensorData> getSensorsHistoryByFaculty(
      String facultyName, LocalDateTime startDate, LocalDateTime endDate) {
    List<Sensor> sensors = sensorRepository.findByLocationFacultyName(facultyName);
    return sensorDataRepository.findBySensorInAndTimestampBetween(sensors, startDate, endDate);
  }

  @Override
  public Sensor addSensor(Sensor sensor) {
    return sensorRepository.save(sensor);
  }

  @Override
  public Sensor updateSensor(Sensor sensor) {
    if (!sensorRepository.existsById(sensor.getSensorId())) {
      throw new SensorNotFoundException(sensor.getSensorId());
    }
    return sensorRepository.save(sensor);
  }

  @Override
  public void deleteSensor(Short sensorId) {
    if (!sensorRepository.existsById(sensorId)) {
      throw new SensorNotFoundException(sensorId);
    }
    sensorRepository.deleteById(sensorId);
  }
}
