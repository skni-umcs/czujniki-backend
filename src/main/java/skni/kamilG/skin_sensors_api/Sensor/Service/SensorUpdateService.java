package skni.kamilG.skin_sensors_api.Sensor.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Influx.IInfluxService;
import skni.kamilG.skin_sensors_api.LiveData.ISseEmitterService;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.Mapper.SensorMapper;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorData;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorStatus;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorUpdateFailure;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorRepository;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorUpdateFailureRepository;

/**
 * Executes async process of updating all sensors current data.
 *
 * @see ISensorUpdateService for definition of the methods
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SensorUpdateService implements ISensorUpdateService {

  private final SensorRepository sensorRepository;
  private final SensorUpdateFailureRepository sensorUpdateFailureRepository;
  private final ISseEmitterService sseEmitterService;
  private final SensorMapper sensorMapper;
  private final IInfluxService influxService;
  private final Clock clock;

  @Value("${scheduler.default-rate:180}")
  private short defaultRate;

  /** Finds sensors that should be updated. */
  @Override
  public List<Sensor> findSensorsToUpdate() {
    return sensorRepository.findByStatusNotOrderById(SensorStatus.OFFLINE);
  }

  /**
   * Updates a single sensor's data and broadcasts the changes. This method is typically called by a
   * scheduler at regular intervals.
   */
  @Override
  public void updateSingleSensor(Sensor sensor) {
    try {
      Optional<SensorData> currentReading =
          influxService.fetchLatestData(
              sensor.getId(),
              sensor.getRefreshRate() == null ? defaultRate : sensor.getRefreshRate());

      if (currentReading.isPresent()) {
        SensorData sensorData = currentReading.get();

        sensor.updateFromSensorData(sensorData, clock);

        if (sensor.getStatus() == SensorStatus.ERROR) {
          recoverSensorFromError(sensor);
        }

        Sensor updatedSensor = sensorRepository.save(sensor);
        SensorResponse sensorResponse = sensorMapper.mapToSensorResponse(updatedSensor);
        sseEmitterService.broadcastSensorUpdate(sensorResponse);

        log.debug("Updated and broadcast sensor {}", sensor.getId());
      } else {
        recordUpdateFailure(sensor);
      }
    } catch (Exception e) {
      log.error("Error updating sensor {}: {}", sensor.getId(), e.getMessage(), e);
    }
  }

  /**
   * Records an update failure when a sensor fails to send data. Also broadcasts the status change
   * to clients.
   */
  private void recordUpdateFailure(Sensor sensor) {
    if (sensorUpdateFailureRepository.existsBySensorIdAndResolvedTimeIsNull(sensor.getId())) {
      log.debug("Sensor {} already has unresolved update failure", sensor.getId());
      return;
    }

    String warning = String.format("Sensor %d didn't send latest data", sensor.getId());
    log.warn(warning);

    sensor.setStatus(SensorStatus.ERROR);

    sensorUpdateFailureRepository.save(
        new SensorUpdateFailure(ZonedDateTime.now(clock), warning, sensor));

    Sensor updatedSensor = sensorRepository.save(sensor);

    SensorResponse sensorResponse = sensorMapper.mapToSensorResponse(updatedSensor);
    sseEmitterService.broadcastSensorUpdate(sensorResponse);

    log.warn(
        "Error during sensorId: {} update, status changed to ERROR and broadcast", sensor.getId());
  }

  /** Recovers a sensor from error state after receiving data. */
  private void recoverSensorFromError(Sensor sensor) {
    sensor.setStatus(SensorStatus.ONLINE);

    SensorUpdateFailure failureToChange =
        sensorUpdateFailureRepository.getBySensorIdAndResolvedTimeIsNull(sensor.getId());
    failureToChange.setResolvedTime(ZonedDateTime.now(clock));
    sensorUpdateFailureRepository.save(failureToChange);

    log.info("Sensor {} is back online", sensor.getId());
  }
}
