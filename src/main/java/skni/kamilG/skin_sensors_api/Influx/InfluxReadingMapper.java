package skni.kamilG.skin_sensors_api.Influx;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import skni.kamilG.skin_sensors_api.Sensor.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorData;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorRepository;

@Component
public class InfluxReadingMapper {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;

  public InfluxReadingMapper(
      SensorRepository sensorRepository, SensorDataRepository sensorDataRepository) {
    this.sensorRepository = sensorRepository;
    this.sensorDataRepository = sensorDataRepository;
  }

  @Transactional
  public SensorData toSensorData(InfluxReading reading) {
    Sensor sensor =
        sensorRepository
            .findById(reading.getSensorId())
            .orElseThrow(() -> new SensorNotFoundException(reading.getSensorId()));
    ZonedDateTime zonedDateTime = reading.getTime().atZone(ZoneId.of("Europe/Warsaw"));
    SensorData sensorData =
        SensorData.builder()
            .sensor(sensor)
            .timestamp(zonedDateTime.toLocalDateTime())
            .temperature(reading.getTemperature())
            .humidity(reading.getHumidity())
            .pressure(reading.getPressure())
            .build();

    return sensorDataRepository.save(sensorData);
  }
}
