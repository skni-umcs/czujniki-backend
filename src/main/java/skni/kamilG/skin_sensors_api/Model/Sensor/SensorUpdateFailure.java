package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "sensor_update_failures")
public class SensorUpdateFailure {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "sensor_id", nullable = false)
  private Short sensorId;

  @Column(name = "failure_time", nullable = false)
  private LocalDateTime failureTime;

  @Column(name = "error_message", nullable = false, length = 200)
  private String errorMessage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sensor_id", insertable = false, updatable = false)
  private Sensor sensor;

  public SensorUpdateFailure(
      Short sensorId, String errorMessage, LocalDateTime failureTime, Sensor sensor) {
    this.sensorId = sensorId;
    this.errorMessage = errorMessage;
    this.failureTime = failureTime;
    this.sensor = sensor;
  }
}
