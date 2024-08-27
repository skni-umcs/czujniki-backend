package skni.kamilG.skin_sensors_api.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(name = "timestamp", nullable = false)
    private ZonedDateTime timestamp;

    @Column(name = "temperature", nullable = false)
    private Integer temperature;

    @Column(name = "humidity", nullable = false)
    private Integer humidity;

    @Column(name = "pressure", nullable = false)
    private Integer pressure;

    @Column(name = "gas_resistance", nullable = false)
    private Integer gasResistance;
}
