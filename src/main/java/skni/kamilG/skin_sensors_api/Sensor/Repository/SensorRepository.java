package skni.kamilG.skin_sensors_api.Sensor.Repository;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorStatus;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Short> {
  @NonNull
  Optional<Sensor> findById(@NotNull Short id);

  List<Sensor> findByStatusNotOrderById(SensorStatus status);

  List<Sensor> findByLocationFacultyAbbreviation(String facultyName);

  /**
   * Searches for sensors based on text input with sorting - OFFLINE sensors at the end. Searches by
   * ID, faculty name, faculty abbreviation, and floor.
   */
  @Query(
      value =
          """
        SELECT s FROM Sensor s
        JOIN s.location l
        WHERE CAST(s.id AS string) = :searchTerm
           OR LOWER(l.facultyName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(l.facultyAbbreviation) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR CAST(s.floor AS string) = :searchTerm
        ORDER BY
           CASE WHEN s.status = 'OFFLINE' THEN 1 ELSE 0 END,
           s.id
        """,
      countQuery =
          """
        SELECT COUNT(s) FROM Sensor s
        JOIN s.location l
        WHERE CAST(s.id AS string) = :searchTerm
           OR LOWER(l.facultyName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(l.facultyAbbreviation) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR CAST(s.floor AS string) = :searchTerm
        """)
  Page<Sensor> searchSensors(@Param("searchTerm") String searchTerm, Pageable pageable);
}
