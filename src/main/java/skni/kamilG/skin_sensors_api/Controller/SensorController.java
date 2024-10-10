package skni.kamilG.skin_sensors_api.Controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skni.kamilG.skin_sensors_api.Exception.NoSensorDataFoundException;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;
import skni.kamilG.skin_sensors_api.Service.ISensorService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {
  private final ISensorService sensorService;

  public SensorController(ISensorService sensorService) {
    this.sensorService = sensorService;
  }

  @GetMapping("/{id}")
  public Sensor getSensorById(@PathVariable Short id) {
    return sensorService.getSensorById(id);
  }

  @GetMapping("/all")
  public List<Sensor> getAllSensors() {
    return sensorService.getAllSensors();
  }

  @GetMapping("/{id}/data/{startDate}/{endDate}")
  public ResponseEntity<List<SensorData>> getSensorDataById(
      @PathVariable Short id,
      @PathVariable("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDateTime startDate,
      @PathVariable("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDateTime endDate) {

    List<SensorData> sensorData = sensorService.getSensorDataById(id, startDate, endDate);
    return ResponseEntity.ok(sensorData);
  }

  @GetMapping("/all/data/{startDate}/{endDate}")
  public ResponseEntity<List<SensorData>> getAllSensorData(
      @PathVariable("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDateTime startDate,
      @PathVariable("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDateTime endDate) {
    List<SensorData> sensorsData = sensorService.getAllSensorsData(startDate, endDate);
    return ResponseEntity.ok(sensorsData);
  }

  @GetMapping("all/faculty/{facultyName}")
  public List<Sensor> getAllSensorsByFaculty(@PathVariable String facultyName) {
    return sensorService.getSensorsByFaculty(facultyName);
  }

  @GetMapping("/all/data/{startDate}/{endDate}/faculty/{facultyName}")
  public ResponseEntity<List<SensorData>> getAllSensorsByFacultyData(
      @PathVariable String facultyName,
      @PathVariable("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDateTime startDate,
      @PathVariable("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDateTime endDate) {

    List<SensorData> sensorsData =
        sensorService.getSensorsDataByFaculty(facultyName, startDate, endDate);

    return ResponseEntity.ok(sensorsData);
  }

  @ExceptionHandler(NoSensorDataFoundException.class)
  public ResponseEntity<Void> handleNoSensorDataException() {
    return ResponseEntity.noContent().build();
  }
}
