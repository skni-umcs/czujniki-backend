package skni.kamilG.skin_sensors_api.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

  @Query(
      "SELECT sd FROM SensorData sd "
          + "WHERE sd.sensor.id IN :sensorIds "
          + "AND (sd.sensor.id, sd.timestamp) IN "
          + "(SELECT s.id, MAX(sd2.timestamp) "
          + " FROM SensorData sd2 "
          + " JOIN sd2.sensor s "
          + " WHERE s.id IN :sensorIds "
          + " GROUP BY s.id)")
  List<SensorData> findLatestDataBySensorIds(@Param("sensorIds") List<Short> sensorIds);

  Optional<Page<SensorData>> findBySensorIdAndTimestampBetween(
          Short sensorId, LocalDateTime start, LocalDateTime end, Pageable pageable);

  Optional<Page<SensorData>> findByTimestampBetween(
      LocalDateTime start, LocalDateTime end, Pageable pageable);

  Optional<Page<SensorData>> findBySensorInAndTimestampBetween(
      List<Sensor> sensors, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
