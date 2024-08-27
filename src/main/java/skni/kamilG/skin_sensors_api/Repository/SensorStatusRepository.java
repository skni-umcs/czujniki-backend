package skni.kamilG.skin_sensors_api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skni.kamilG.skin_sensors_api.Model.SensorStatus;

public interface SensorStatusRepository extends JpaRepository<SensorStatus, Long> {
}
