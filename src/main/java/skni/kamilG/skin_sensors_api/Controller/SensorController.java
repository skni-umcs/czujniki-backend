package skni.kamilG.skin_sensors_api.Controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import skni.kamilG.skin_sensors_api.Exception.NoSensorDataFoundException;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;
import skni.kamilG.skin_sensors_api.Service.ISensorService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@Validated
public class SensorController {
  private final ISensorService sensorService;

  public SensorController(ISensorService sensorService) {
    this.sensorService = sensorService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Sensor> getSensorById(@PathVariable Short id) {
    return ResponseEntity.ok(sensorService.getSensorById(id));
  }

  @GetMapping
  public ResponseEntity<List<Sensor>> getAllSensors() {
    return ResponseEntity.ok(sensorService.getAllSensors());
  }

  @GetMapping("/{id}/data")
  public ResponseEntity<List<SensorData>> getSensorDataById(
      @PathVariable Short id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime endDate) {
    return ResponseEntity.ok(sensorService.getSensorDataById(id, startDate, endDate));
  }

  @GetMapping("/data")
  public ResponseEntity<List<SensorData>> getAllSensorData(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime endDate) {
    return ResponseEntity.ok(sensorService.getAllSensorsData(startDate, endDate));
  }

  @GetMapping("/faculty/{facultyName}")
  public List<Sensor> getAllSensorsByFaculty(@PathVariable String facultyName) {
    return sensorService.getSensorsByFaculty(facultyName);
  }

  @GetMapping("/faculty/{facultyName}")
  public ResponseEntity<List<SensorData>> getAllSensorsByFacultyData(
      @PathVariable String facultyName,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime endDate) {
    return ResponseEntity.ok(
        sensorService.getSensorsDataByFaculty(facultyName, startDate, endDate));
  }

  @ExceptionHandler(NoSensorDataFoundException.class)
  public ResponseEntity<Void> handleNoSensorDataException() {
    return ResponseEntity.noContent().build();
  }
}
