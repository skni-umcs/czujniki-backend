package skni.kamilG.skin_sensors_api.Controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorDTO;
import skni.kamilG.skin_sensors_api.Service.ISensorService;
import skni.kamilG.skin_sensors_api.Service.ISensorUpdateService;

@RestController
@RequestMapping("admin/api/sensors")
@AllArgsConstructor
public class AdminController {

  private final ISensorService sensorService;
  private final ISensorUpdateService sensorUpdater;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SensorDTO> createSensor(@RequestBody @Valid SensorDTO request) {
    Sensor createdSensor = sensorService.createSensor(request);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdSensor.getId())
            .toUri();

    return ResponseEntity.created(location).build();
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateSensor(
      @RequestBody @Validated SensorDTO updatedDTO, @PathVariable Short id) {
    Sensor updatedSensor = sensorService.updateSensor(updatedDTO, id);
    return ResponseEntity.ok(updatedSensor);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteSensor(@PathVariable Short id) {
    sensorService.deleteSensor(id);
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PostMapping("/all")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> forceUpdateSensorsData(
      @RequestParam(defaultValue = "false") boolean forceUpdate) {
    if (forceUpdate) {
      sensorUpdater.forceUpdateSensorsData();
      return ResponseEntity.ok(HttpStatus.OK);
    }
    return ResponseEntity.ok(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
