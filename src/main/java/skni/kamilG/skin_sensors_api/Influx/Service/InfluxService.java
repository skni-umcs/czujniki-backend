package skni.kamilG.skin_sensors_api.Influx.Service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Influx.Model.InfluxReading;
import skni.kamilG.skin_sensors_api.Influx.Model.InfluxReadingMapper;
import skni.kamilG.skin_sensors_api.Influx.Repository.InfluxReadingRepository;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorData;

@Slf4j
@Service
@AllArgsConstructor
public class InfluxService implements IInfluxService {

  public InfluxReadingRepository readingRepository;
  public InfluxReadingMapper influxReadingMapper;

  @Override
  public Optional<SensorData> fetchLatestData(Short sensorId, Short refreshRate) {
    Optional<InfluxReading> latestReading =
        readingRepository.getLatestReading(sensorId, refreshRate);
    if (latestReading.isPresent()) {
      log.debug("Fetched latest data for sensor {}", sensorId);
      return Optional.of(influxReadingMapper.toSensorData(latestReading.get()));
    } else {
      log.warn("No data found for sensor {}", sensorId);
      return Optional.empty();
    }
  }
}
