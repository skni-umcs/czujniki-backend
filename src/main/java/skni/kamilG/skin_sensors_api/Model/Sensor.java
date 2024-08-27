package skni.kamilG.skin_sensors_api.Model;


import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "location")
    private String location;

    @Column(name = "current_temperature")
    private Integer currentTemperature;

    @Column(name = "current_humidity")
    private Integer currentHumidity;

    @Column(name = "current_pressure")
    private Integer currentPressure;

    @Column(name = "current_gas_resistance")
    private Integer currentGasResistance;

    private Department department;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SensorData> sensorData = new HashSet<>();

    @Transient
    private SensorStatus status;

    public void updateCurrentValues(SensorData data) {
        this.currentTemperature = data.getTemperature();
        this.currentHumidity = data.getHumidity();
        this.currentPressure = data.getPressure();
        this.currentGasResistance = data.getGasResistance();
    }
}

