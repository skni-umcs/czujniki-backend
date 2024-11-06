package skni.kamilG.skin_sensors_api.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import skni.kamilG.skin_sensors_api.Exception.InvalidDateRangeException;
import skni.kamilG.skin_sensors_api.Exception.NoSensorsForFacultyException;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorDTO;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;
import skni.kamilG.skin_sensors_api.Service.ISensorService;

import java.awt.print.Pageable;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@Validated
@RequiredArgsConstructor
public class SensorController {

  private final ISensorService sensorService;

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
  public ResponseEntity<Page<SensorData>> getAllSensorData(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime endDate,
      @PageableDefault(size = 8, sort = "timestamp", direction = Sort.Direction.DESC)
          Pageable pageable) {
    Page<SensorData> sensorDataPage = sensorService.getAllSensorsData(startDate, endDate, pageable);
    return ResponseEntity.ok(sensorDataPage);
  }

  @GetMapping("/faculty/{facultyName}")
  public ResponseEntity<List<Sensor>> getAllSensorsByFaculty(@PathVariable String facultyName) {
    return ResponseEntity.ok(sensorService.getSensorsByFaculty(facultyName));
  }

  @GetMapping("/faculty/{facultyName}")
  public ResponseEntity<Page<SensorData>> getAllSensorsByFacultyData(
      @PathVariable String facultyName,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          LocalDateTime endDate,
      @PageableDefault(size = 8, sort = "timestamp", direction = Sort.Direction.DESC)
          Pageable pageable) {
    validateDateRange(startDate, endDate);
    Page<SensorData> sensorDataPage =
        sensorService.getSensorsDataByFaculty(facultyName, startDate, endDate, pageable);
    return ResponseEntity.ok(sensorDataPage);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SensorDTO> createSensor(@RequestBody @Valid SensorDTO request) {
    Sensor createdSensor = sensorService.createSensor(request);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdSensor.getSensorId())
            .toUri();

    return ResponseEntity.created(location).build();
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateSensor(@RequestBody @Valid SensorDTO updatedDTO, @PathVariable Short id) {
    sensorService.updateSensor(updatedDTO, id);
    return ResponseEntity.noContent().build();
  }


  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteSensor(@PathVariable Short id) {
    sensorService.deleteSensor(id);
    return ResponseEntity.noContent().build();
  }

  private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate.isAfter(endDate)) {
      throw new InvalidDateRangeException("Start date must be before end date");
    }
  }
}
