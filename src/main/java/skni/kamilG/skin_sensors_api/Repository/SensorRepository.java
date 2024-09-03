package skni.kamilG.skin_sensors_api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorStatus;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Short> {
    List<Sensor> findByStatus(SensorStatus status);
    List<Sensor> findByLocationFacultyName(String facultyName);

}
