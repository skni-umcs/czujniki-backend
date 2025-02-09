package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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

  private LocalDateTime timestamp;

  private Short temperature;

  private Integer humidity;

  private Integer pressure;

  private Integer gasResistance;
}
