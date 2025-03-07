package skni.kamilG.skin_sensors_api.Influx;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Measurement(name = "sensor_readings")
public class InfluxReading {

  @Column(timestamp = true)
  private Instant time;

  @Column private Short sensorId;

  @Column
  @Setter(AccessLevel.NONE)
  private Float temperature;

  @Column private Integer humidity;

  @Setter(AccessLevel.NONE)
  @Column
  private Integer pressure;

  public void setTemperature(Short temperature) {
    this.temperature = (float) (temperature / 100);
  }

  public void setPressure(Integer pressure) {
    this.pressure = pressure / 100;
  }
}
