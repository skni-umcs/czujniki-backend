package skni.kamilG.skin_sensors_api.Influx;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
  private BigDecimal temperature;

  @Column private Integer humidity;

  @Setter(AccessLevel.NONE)
  @Column
  private Integer pressure;

  public void setTemperature(Short temperatureRaw) {
    this.temperature =
        new BigDecimal(temperatureRaw).divide(new BigDecimal(100), 1, RoundingMode.HALF_UP);
  }

  public void setPressure(Integer pressure) {
    this.pressure = pressure / 10;
  }
}
