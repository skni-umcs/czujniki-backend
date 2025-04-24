package skni.kamilG.skin_sensors_api.Sensor.Controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import skni.kamilG.skin_sensors_api.Sensor.Exception.InvalidDateRangeException;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorDataResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Sensor.Service.ISensorService;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

  private final ISensorService sensorService;
  private final Clock clock;

  @GetMapping("/{id}")
  public ResponseEntity<SensorResponse> getSensorById(@PathVariable Short id) {
    return ResponseEntity.ok(sensorService.getSensorById(id));
  }

  @GetMapping
  public ResponseEntity<Page<SensorResponse>> getAllSensors(
      @RequestParam(required = false) String searchTerm,
      @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

    if (searchTerm != null && !searchTerm.isBlank()) {
      return ResponseEntity.ok(sensorService.searchSensors(searchTerm, pageable));
    }
    return ResponseEntity.ok(sensorService.findAllWithCustomSorting(pageable));
  }

  @GetMapping("/{id}/data")
  public ResponseEntity<Page<SensorDataResponse>> getSensorDataById(
      @PathVariable Short id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          ZonedDateTime startDate,
      @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          @PastOrPresent
          ZonedDateTime endDate,
      @PageableDefault(size = 8, sort = "timestamp", direction = Sort.Direction.DESC)
          Pageable pageable) {
    ZonedDateTime endDateToFilter = endDate != null ? endDate : ZonedDateTime.now(clock);
    validateDateRange(startDate, endDateToFilter);
    return ResponseEntity.ok(
        sensorService.getSensorDataById(id, startDate, endDateToFilter, pageable));
  }

  @GetMapping("/data")
  public ResponseEntity<Page<SensorDataResponse>> getAllSensorData(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          ZonedDateTime startDate,
      @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          @PastOrPresent
          ZonedDateTime endDate,
      @PageableDefault(size = 8, sort = "timestamp", direction = Sort.Direction.DESC)
          Pageable pageable) {
    ZonedDateTime endDateToFilter = endDate != null ? endDate : ZonedDateTime.now(clock);
    validateDateRange(startDate, endDateToFilter);
    return ResponseEntity.ok(sensorService.getAllSensorsData(startDate, endDateToFilter, pageable));
  }

  @GetMapping("/faculty/{facultyName}")
  public ResponseEntity<List<SensorResponse>> getAllSensorsByFaculty(
      @PathVariable String facultyName) {
    return ResponseEntity.ok(sensorService.getSensorsByFaculty(facultyName));
  }

  @GetMapping("/faculty/{facultyName}/data")
  public ResponseEntity<Page<SensorDataResponse>> getAllSensorsByFacultyData(
      @PathVariable String facultyName,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
          ZonedDateTime startDate,
      @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          @PastOrPresent
          ZonedDateTime endDate,
      @PageableDefault(size = 8, sort = "timestamp", direction = Sort.Direction.DESC)
          Pageable pageable) {
    ZonedDateTime endDateToFilter = endDate != null ? endDate : ZonedDateTime.now(clock);
    validateDateRange(startDate, endDateToFilter);
    return ResponseEntity.ok(
        sensorService.getSensorsDataByFaculty(facultyName, startDate, endDateToFilter, pageable));
  }

  private void validateDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
    if (startDate.isAfter(endDate)) {
      throw new InvalidDateRangeException("Start date must be before end date");
    }
  }
}
