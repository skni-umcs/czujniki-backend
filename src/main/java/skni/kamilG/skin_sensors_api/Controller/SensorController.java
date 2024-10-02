package skni.kamilG.skin_sensors_api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skni.kamilG.skin_sensors_api.Model.DateRange;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;
import skni.kamilG.skin_sensors_api.Service.ISensorService;

import java.util.List;
import java.util.Optional;

// TODO skonsultowanie DTO
@RestController
@RequestMapping("/api/sensor")
public class SensorController {
  private final ISensorService sensorService;

  @Autowired
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

  @GetMapping("/{id}/data")
  public ResponseEntity<List<SensorData>> getSensorDataById(
      @RequestBody DateRange dataRange, @PathVariable Short id) {
    Optional<List<SensorData>> sensorData =
        sensorService.getSensorDataById(id, dataRange.getStart(), dataRange.getEnd());
    return sensorData
        .map(data -> ResponseEntity.ok().body(data))
        .orElseGet(() -> ResponseEntity.noContent().build());
  }

  @GetMapping("/all/data")
  public ResponseEntity<List<SensorData>> getAllSensorData(@RequestParam DateRange dataRange) {
    Optional<List<SensorData>> sensorsData =
        sensorService.getAllSensorsData(dataRange.getStart(), dataRange.getEnd());
    return sensorsData
        .map(data -> ResponseEntity.ok().body(data))
        .orElseGet(() -> ResponseEntity.noContent().build());
  }

  @GetMapping("/faculty/{facultyName}/all")
  public List<Sensor> getAllSensorsByFaculty(@PathVariable String facultyName) {
    return sensorService.getSensorsByFaculty(facultyName);
  }

  @GetMapping("/faculty/{facultyName}/all/data")
  public ResponseEntity<List<SensorData>> getAllSensorsByFacultyData(
      @RequestParam DateRange dataRange, @PathVariable String facultyName) {
    Optional<List<SensorData>> sensorsData =
        sensorService.getSensorsDataByFaculty(
            facultyName, dataRange.getStart(), dataRange.getEnd());
    return sensorsData
        .map(data -> ResponseEntity.ok().body(data))
        .orElseGet(() -> ResponseEntity.noContent().build());
  }
}
