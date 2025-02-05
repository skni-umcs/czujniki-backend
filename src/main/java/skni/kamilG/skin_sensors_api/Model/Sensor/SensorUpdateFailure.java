package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "sensor_update_failure")
@Setter
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
