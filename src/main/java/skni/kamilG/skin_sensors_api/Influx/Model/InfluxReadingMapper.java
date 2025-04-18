package skni.kamilG.skin_sensors_api.Influx.Model;

import java.time.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import skni.kamilG.skin_sensors_api.Sensor.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorData;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorRepository;

@Component
@AllArgsConstructor
public class InfluxReadingMapper {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;

  public SensorData toSensorData(InfluxReading reading) {

    Sensor sensor =
        sensorRepository
            .findById(reading.getSensorId())
            .orElseThrow(() -> new SensorNotFoundException(reading.getSensorId()));
    SensorData sensorData =
        SensorData.builder()
            .sensor(sensor)
            .timestamp(reading.getTime().atZone(ZoneOffset.UTC))
            .temperature(reading.getTemperature())
            .humidity(reading.getHumidity())
            .pressure(reading.getPressure())
            .build();

    return sensorDataRepository.save(sensorData);
  }
}
