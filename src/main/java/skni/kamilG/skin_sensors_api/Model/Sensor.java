package skni.kamilG.skin_sensors_api.Model;


import jakarta.persistence.*;
import lombok.*;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "sensors")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_id")
    private Short sensorId;

    private SensorStatus status;

    private LocalDateTime latestDataUpdate;

    @Transient
    private short currentTemperature;

    @Transient
    private long currentHumidity;

    @Transient
    private long currentPressure;

    @Transient
    private long currentGasResistance;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SensorData> sensorData = new HashSet<>();

    public void setCurrentData(SensorData latestSensorData, LocalDateTime latestDataUpdate) {
        this.currentTemperature = latestSensorData.getTemperature();
        this.currentHumidity = latestSensorData.getHumidity();
        this.currentPressure = latestSensorData.getPressure();
        this.currentGasResistance = latestSensorData.getGasResistance();
        this.latestDataUpdate = latestDataUpdate;
    }

}

