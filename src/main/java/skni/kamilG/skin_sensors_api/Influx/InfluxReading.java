package skni.kamilG.skin_sensors_api.Influx;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Measurement(name = "sensor_readings")
public class InfluxReading {

  @Column(timestamp = true)
  private Instant time;

  @Column private Short sensorId;

  @Column private Short temperature;

  @Column private Integer humidity;

  @Column private Integer pressure;
}
