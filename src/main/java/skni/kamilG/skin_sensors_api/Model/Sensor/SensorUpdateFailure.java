package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "sensor_update_failure")
public class SensorUpdateFailure {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NonNull private LocalDateTime failureTime;

  @NonNull private String errorMessage;

  @NonNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sensor_id", insertable = false, updatable = false)
  private Sensor sensor;
}
