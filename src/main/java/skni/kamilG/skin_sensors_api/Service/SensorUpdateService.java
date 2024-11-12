package skni.kamilG.skin_sensors_api.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import skni.kamilG.skin_sensors_api.Exception.SensorUpdateException;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorStatus;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorUpdateFailure;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorUpdateFailureRepository;

@Slf4j
@Service
public class SensorUpdateService implements ISensorUpdateService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;
  private final SensorUpdateFailureRepository sensorUpdateFailureRepository;
  private final Sinks.Many<Sensor> sensorUpdatesSink =
      Sinks.many().multicast().onBackpressureBuffer();

  @Autowired
  public SensorUpdateService(
      SensorRepository sensorRepository,
      SensorDataRepository sensorDataRepository,
      SensorUpdateFailureRepository sensorUpdateFailureRepository) {
    this.sensorRepository = sensorRepository;
    this.sensorDataRepository = sensorDataRepository;
    this.sensorUpdateFailureRepository = sensorUpdateFailureRepository;
  }

  @Override
  public void forceUpdateSensorsData() {
    performSensorDataUpdate();
  }

  @Async
  @Scheduled(fixedRate = 40000) // TODO to discuss with firmware team
  @Override
  @SneakyThrows(Exception.class)
  public void updateSensorsData() {
    List<Sensor> updatedSensors = performSensorDataUpdate();
    updatedSensors.forEach(sensorUpdatesSink::tryEmitNext);
  }

  @Override
  @SneakyThrows(SensorUpdateException.class)
  public List<Sensor> performSensorDataUpdate() {
    log.info("Starting sensor data update process");

    List<Sensor> correctlyUpdatedSensors = new ArrayList<>();
    List<Sensor> sensors = sensorRepository.findByStatus(SensorStatus.ONLINE);
    List<Short> sensorIds = sensors.stream().map(Sensor::getId).collect(Collectors.toList());
    List<SensorUpdateFailure> updateFailures = new ArrayList<>();

    log.debug("Found {} online sensors with IDs: {}", sensors.size(), sensorIds);

    Map<Short, SensorData> latestData = sensorDataRepository.findLatestDataBySensorIds(sensorIds);

    LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(60);

    for (Sensor sensor : sensors) {
      try {
        SensorData latestSensorData = latestData.get(sensor.getId());
        if (latestSensorData != null) {
          if (latestSensorData.getTimestamp().isAfter(thresholdTime)) {
            sensor.setCurrentData(latestSensorData);
            correctlyUpdatedSensors.add(sensor);
            log.debug("Updated sensor {} with latest data", sensor.getId());
          } else {
            String warning =
                String.format(
                    "Sensor %d has outdated data (last updated at %s), setting status to ERROR",
                    sensor.getId(), latestSensorData.getTimestamp());
            log.warn(warning);
            sensor.setStatus(SensorStatus.ERROR);
            updateFailures.add(
                new SensorUpdateFailure(sensor.getId(), warning, LocalDateTime.now(), sensor));
          }
        } else {
          String warning =
              String.format(
                  "Sensor %d has no latest data, setting status to ERROR", sensor.getId());
          log.warn(warning);
          sensor.setStatus(SensorStatus.ERROR);
          updateFailures.add(
              new SensorUpdateFailure(sensor.getId(), warning, LocalDateTime.now(), sensor));
        }
        sensorRepository.save(sensor);
      } catch (Exception e) {
        log.error("Error updating sensor {} data", sensor.getId(), e);
        throw new SensorUpdateException(e);
      }
    }
    if (!updateFailures.isEmpty()) {
      sensorUpdateFailureRepository.saveAll(updateFailures);
      log.warn("{} sensor update failures recorded", updateFailures.size());
    }
    log.info("Sensor data update process completed");
    return correctlyUpdatedSensors;
  }

  public Flux<Sensor> getAllSensorsUpdates() {
    return sensorUpdatesSink.asFlux();
  }

  public Flux<Sensor> getSensorUpdates(Short sensorId) {
    return getAllSensorsUpdates().filter(sensor -> sensor.getId().equals(sensorId));
  }
}
