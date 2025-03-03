package skni.kamilG.skin_sensors_api.Service;

import static org.apache.commons.collections4.ListUtils.partition;

import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skni.kamilG.skin_sensors_api.Exception.InfluxProcessingException;
import skni.kamilG.skin_sensors_api.Model.Sensor.InfluxReading;
import skni.kamilG.skin_sensors_api.Model.Sensor.Mapper.InfluxReadingMapper;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;
import skni.kamilG.skin_sensors_api.Repository.InfluxReadingRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxService implements IInfluxService {

  @NonNull private final InfluxReadingRepository influxReadingRepository;
  @NonNull private final InfluxReadingMapper influxReadingMapper;
  @NonNull private final SensorDataRepository sensorDataRepository;

  @Value("${batch.size}")
  private int batchSize;

  @Override
  @Transactional
  public void fetchLatestData() {
    try {
      log.info("Starting data fetch from InfluxDB");
      List<InfluxReading> influxLatestReadings = influxReadingRepository.getLatestReadings();

      List<SensorData> sensorDataList =
          influxLatestReadings.stream()
              .map(influxReadingMapper::toSensorData)
              .collect(Collectors.toList());

      partition(sensorDataList, batchSize)
          .forEach(
              batch -> {
                try {
                  sensorDataRepository.saveAll(batch);
                  log.debug("Saved batch of {} records", batch.size());
                } catch (Exception e) {
                  log.error("Error saving batch to PostgreSQL's DB", e);
                  throw new InfluxProcessingException("Failed to save data to PostgreSQL's DB", e);
                }
              });

      log.info("Successfully processed {} readings", influxLatestReadings.size());
    } catch (Exception e) {
      log.error("Error during data fetch and processing", e);
      throw new InfluxProcessingException("Failed to process InfluxDB data ", e);
    }
  }
}
