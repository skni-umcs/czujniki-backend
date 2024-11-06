package skni.kamilG.skin_sensors_api.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorStatus;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;

@Service
public class SensorUpdateService implements ISensorUpdateService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;

  @Autowired
  public SensorUpdateService(
      SensorRepository sensorRepository, SensorDataRepository sensorDataRepository) {
    this.sensorRepository = sensorRepository;
    this.sensorDataRepository = sensorDataRepository;
  }

  @Scheduled(fixedRate = 60000)
  @Override
  public void updateSensorsData() {
    performSensorDataUpdate();
  }

  @Override
  public void forceUpdateSensorsData() {
    performSensorDataUpdate();
  }

  private void performSensorDataUpdate() {
    List<Sensor> sensors = sensorRepository.findByStatus(SensorStatus.ONLINE);
    for (Sensor sensor : sensors) {
      sensorDataRepository
          .findTopBySensorOrderByTimestampDesc(sensor)
          .ifPresentOrElse(
                  sensor::setCurrentData,
              () -> sensor.setStatus(SensorStatus.OFFLINE));
      sensorRepository.save(sensor);
    }
  }
}
