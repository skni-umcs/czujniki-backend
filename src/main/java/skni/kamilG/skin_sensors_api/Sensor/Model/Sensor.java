package skni.kamilG.skin_sensors_api.Sensor.Model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "sensor")
public class Sensor implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Short id;

  @Enumerated(EnumType.STRING)
  private SensorStatus status;

  private ZonedDateTime lastUpdate;

  @Column(precision = 5, scale = 1)
  private BigDecimal temperature;

  private Integer humidity;

  private Integer pressure;

  private Short refreshRate;

  private Short floor;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "location_id", referencedColumnName = "id")
  private Location location;

  public void updateFromSensorData(SensorData latestSensorData, Clock clock) {
    this.temperature = latestSensorData.getTemperature();
    this.humidity = latestSensorData.getHumidity();
    this.pressure = latestSensorData.getPressure();
    this.lastUpdate = ZonedDateTime.now(clock);
  }
}
