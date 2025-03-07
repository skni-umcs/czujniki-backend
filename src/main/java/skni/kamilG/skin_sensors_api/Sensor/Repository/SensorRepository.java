package skni.kamilG.skin_sensors_api.Sensor.Repository;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorStatus;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Short> {
  @NonNull
  Optional<Sensor> findById(@NotNull Short id);

  List<Sensor> findByStatusNotOrderById(SensorStatus status);

  List<Sensor> findByLocationFacultyAbbreviation(String facultyName);
}
