package skni.kamilG.skin_sensors_api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorUpdateFailure;

public interface SensorUpdateFailureRepository extends JpaRepository<SensorUpdateFailure, Long> {}
