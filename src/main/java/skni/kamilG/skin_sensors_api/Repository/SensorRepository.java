package skni.kamilG.skin_sensors_api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skni.kamilG.skin_sensors_api.Model.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Short> {
}
