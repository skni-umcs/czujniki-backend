package skni.kamilG.skin_sensors_api.Model.Sensor.Mapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import skni.kamilG.skin_sensors_api.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Model.Sensor.InfluxReading;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;

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
            .temperature(reading.getRawTemperature())
            .humidity(reading.getRawHumidity())
            .pressure(reading.getRawPressure())
            .gasResistance(reading.getRawGasResistance())
            .build();

    return sensorDataRepository.save(sensorData);
  }
}
