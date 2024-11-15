package skni.kamilG.skin_sensors_api.Controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import skni.kamilG.skin_sensors_api.Exception.InvalidDateRangeException;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;
import skni.kamilG.skin_sensors_api.Service.ISensorService;
import skni.kamilG.skin_sensors_api.Service.ISensorUpdateService;

@RestController
@RequestMapping("/api/sensors")
@Validated
@RequiredArgsConstructor
public class SensorController {

  private final ISensorService sensorService;

  private final ISensorUpdateService sensorUpdateService;

  @GetMapping("/{id}")
  public ResponseEntity<SensorResponse> getSensorById(@PathVariable Short id) {
    return ResponseEntity.ok(sensorService.getSensorById(id));
  }

  @GetMapping(value = "/{sensorId}/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Sensor> streamSingleSensor(@PathVariable Short sensorId) {
    return sensorUpdateService.getSensorUpdates(sensorId);
  }

  @GetMapping
  public ResponseEntity<List<SensorResponse>> getAllSensors() {
    return ResponseEntity.ok(sensorService.getAllSensors());
  }

  @GetMapping(value = "/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Sensor> streamAllSensorsUpdates() {
    return sensorUpdateService.getAllSensorsUpdates();
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
  public ResponseEntity<List<SensorResponse>> getAllSensorsByFaculty(
      @PathVariable String facultyName) {
    return ResponseEntity.ok(sensorService.getSensorsByFaculty(facultyName));
  }

  @GetMapping("/faculty/{facultyName}/data")
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

  private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate.isAfter(endDate)) {
      throw new InvalidDateRangeException("Start date must be before end date");
    }
  }
}
