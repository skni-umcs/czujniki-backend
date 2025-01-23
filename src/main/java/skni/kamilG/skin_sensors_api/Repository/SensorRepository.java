package skni.kamilG.skin_sensors_api.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorStatus;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Short> {
  List<Sensor> findByStatusNotOrderById(SensorStatus status);

  List<Sensor> findByLocationFacultyName(String facultyName);

}
