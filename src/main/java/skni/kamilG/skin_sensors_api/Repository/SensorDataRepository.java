package skni.kamilG.skin_sensors_api.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
  Optional<SensorData> findTopBySensorOrderByTimestampDesc(Sensor sensor);

  List<SensorData> findBySensor_SensorIdAndTimestampBetween(
      Short sensorId, LocalDateTime start, LocalDateTime end);

  List<SensorData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

  List<SensorData> findBySensorInAndTimestampBetween(
      List<Sensor> sensors, LocalDateTime start, LocalDateTime end);
}
