package skni.kamilG.skin_sensors_api.Sensor.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "sensor_data")
public class SensorData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "data_id")
  private Long dataId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sensor_id", nullable = false)
  private Sensor sensor;

  private ZonedDateTime timestamp;

  @Column(precision = 5, scale = 1)
  private BigDecimal temperature;

  private Integer humidity;

  private Integer pressure;
}
