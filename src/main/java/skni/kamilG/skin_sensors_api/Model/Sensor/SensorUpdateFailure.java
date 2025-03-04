package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
  private LocalDateTime issuedTime;

  private LocalDateTime resolvedTime;

  @NonNull private String errorMessage;

  @NonNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sensor_id")
  private Sensor sensor;
}
