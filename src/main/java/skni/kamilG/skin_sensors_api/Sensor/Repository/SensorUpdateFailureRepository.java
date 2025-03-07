package skni.kamilG.skin_sensors_api.Sensor.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorUpdateFailure;

public interface SensorUpdateFailureRepository extends JpaRepository<SensorUpdateFailure, Long> {
  SensorUpdateFailure getBySensorIdAndResolvedTimeIsNull(Short sensor_id);

  boolean existsBySensorIdAndResolvedTimeIsNull(Short sensor_id);
}
