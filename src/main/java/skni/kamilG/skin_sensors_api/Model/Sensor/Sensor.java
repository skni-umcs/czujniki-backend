package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import skni.kamilG.skin_sensors_api.Model.Location;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "sensors")
public class Sensor implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Short id;

  private SensorStatus status;

  private LocalDateTime latestDataUpdate;

  private Short currentTemperature;

  private Integer currentHumidity;

  private Integer currentPressure;

  private Integer currentGasResistance;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "location_id", referencedColumnName = "id")
  private Location location;

  @OneToMany(
      mappedBy = "sensor",
      cascade = CascadeType.ALL,
      orphanRemoval = false,
      fetch = FetchType.LAZY)
  private Set<SensorData> sensorData = new HashSet<>();

  public void setCurrentData(SensorData latestSensorData) {
    this.currentTemperature = latestSensorData.getTemperature();
    this.currentHumidity = latestSensorData.getHumidity();
    this.currentPressure = latestSensorData.getPressure();
    this.currentGasResistance = latestSensorData.getGasResistance();
    this.latestDataUpdate = LocalDateTime.now();
  }
}
