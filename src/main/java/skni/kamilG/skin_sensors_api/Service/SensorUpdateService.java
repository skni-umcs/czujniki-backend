package skni.kamilG.skin_sensors_api.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Exception.SensorUpdateException;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorStatus;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;

@Service
@Slf4j
public class SensorUpdateService implements ISensorUpdateService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;

  @Autowired
  public SensorUpdateService(
      SensorRepository sensorRepository, SensorDataRepository sensorDataRepository) {
    this.sensorRepository = sensorRepository;
    this.sensorDataRepository = sensorDataRepository;
  }

  @Async
  @Scheduled(fixedRate = 10000) // TODO to discuss with firmware team
  @Override
  @SneakyThrows(Exception.class)
  public void updateSensorsData() {
    performSensorDataUpdate();
  }

  @Override
  public void forceUpdateSensorsData() {
    performSensorDataUpdate();
  }

  @Override
  @SneakyThrows(SensorUpdateException.class)
  public void performSensorDataUpdate() { // TODO handling exception when updating
    log.info("Starting sensor data update process");

    List<Sensor> sensors = sensorRepository.findByStatus(SensorStatus.ONLINE);
    List<Short> sensorIds = sensors.stream().map(Sensor::getId).collect(Collectors.toList());

    log.debug("Found {} online sensors with IDs: {}", sensors.size(), sensorIds);

    Map<Short, SensorData> latestData = sensorDataRepository.findLatestDataBySensorIds(sensorIds);

    for (Sensor sensor : sensors) {
      try {
        SensorData latestSensorData = latestData.get(sensor.getId());
        if (latestSensorData != null) {
          sensor.setCurrentData(latestSensorData);
          log.debug("Updated sensor {} with latest data", sensor.getId());
        } else {
          log.warn("Sensor {} has no latest data, setting status to ERROR", sensor.getId());
          sensor.setStatus(SensorStatus.ERROR);
        }
        sensorRepository.save(sensor);
      } catch (Exception e) {
        log.error("Error updating sensor {} data", sensor.getId(), e);
        throw new SensorUpdateException(
            String.format("Error updating sensor %d data", sensor.getId()), e);
      }
    }
    log.info("Sensor data update process completed");
  }
}
