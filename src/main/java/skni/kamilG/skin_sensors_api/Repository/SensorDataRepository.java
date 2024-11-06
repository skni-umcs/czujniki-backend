package skni.kamilG.skin_sensors_api.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
  Optional<SensorData> findTopBySensorOrderByTimestampDesc(Sensor sensor);

  Optional<List<SensorData>> findBySensor_SensorIdAndTimestampBetween(
      Short sensorId, LocalDateTime start, LocalDateTime end);

  Optional<Page<SensorData>> findByTimestampBetween(
      LocalDateTime start, LocalDateTime end, Pageable pageable);

  Optional<Page<SensorData>> findBySensorInAndTimestampBetween(
      List<Sensor> sensors, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
