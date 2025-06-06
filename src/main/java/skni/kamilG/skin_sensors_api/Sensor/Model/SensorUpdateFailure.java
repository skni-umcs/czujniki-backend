package skni.kamilG.skin_sensors_api.Sensor.Model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.*;

@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "sensor_update_failure")
@Setter
@Getter
public class SensorUpdateFailure {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NonNull
  @Column(nullable = false)
  private ZonedDateTime issuedTime;

  private ZonedDateTime resolvedTime;

  @NonNull private String errorMessage;

  @NonNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sensor_id")
  private Sensor sensor;
}
