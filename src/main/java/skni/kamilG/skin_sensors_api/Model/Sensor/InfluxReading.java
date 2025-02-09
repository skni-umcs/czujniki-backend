package skni.kamilG.skin_sensors_api.Model.Sensor;

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

  @Column private Short rawTemperature;

  @Column private Integer rawHumidity;

  @Column private Integer rawPressure;

  @Column private Integer rawGasResistance;
}
