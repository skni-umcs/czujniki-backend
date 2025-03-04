package skni.kamilG.skin_sensors_api.Controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;


import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import skni.kamilG.skin_sensors_api.Exception.InvalidDateRangeException;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorDataResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Service.ISensorService;
import skni.kamilG.skin_sensors_api.Service.ISensorUpdateService;

@RestController
@RequestMapping("/api/sensor")
@Validated
@RequiredArgsConstructor
@Slf4j
public class SensorController {

    private final Clock clock;

    private final ISensorService sensorService;

    private final ISensorUpdateService sensorUpdateService;

    @GetMapping("/{id}")
    public ResponseEntity<SensorResponse> getSensorById(@PathVariable Short id) {
        return ResponseEntity.ok(sensorService.getSensorById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<SensorResponse>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getAllSensors());
    }

    @GetMapping(value = "/{sensorId}/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamSingleSensor(@PathVariable Short sensorId) {
        Flux<ServerSentEvent<?>> heartbeat = Flux.interval(Duration.ofSeconds(30))
                .map(i -> ServerSentEvent.builder()
                        .comment("heartbeat " + LocalDateTime.now(clock))
                        .build());

        Flux<ServerSentEvent<?>> sensorData = sensorUpdateService.getSensorUpdates(sensorId)
                .map(data -> ServerSentEvent.builder()
                        .data(data)
                        .build());

        return Flux.merge(sensorData, heartbeat)
                .doOnCancel(() -> log.info("Klient rozłączył się od strumienia czujnika {}", sensorId))
                .doOnSubscribe(s -> log.info("Nowy klient połączył się do strumienia czujnika {}", sensorId))
                .onErrorResume(e -> {
                    log.error("Błąd podczas streamowania danych czujnika {}: {}", sensorId, e.getMessage());
                    return Flux.empty();
                });
    }

    @GetMapping(value = "/all/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SensorResponse> streamAllSensorsUpdates() {
        return sensorUpdateService.getAllSensorsUpdates();
    }

    @GetMapping("/{id}/data")
    public ResponseEntity<Page<SensorDataResponse>> getSensorDataById(
            @PathVariable Short id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
            LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
            LocalDateTime endDate,
            @PageableDefault(size = 8, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable) {

        validateDateRange(startDate, endDate);
        return ResponseEntity.ok(sensorService.getSensorDataById(id, startDate, endDate, pageable));
    }

    @GetMapping("/all/data")
    public ResponseEntity<Page<SensorDataResponse>> getAllSensorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
            LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
            LocalDateTime endDate,
            @PageableDefault(size = 8, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable) {
        validateDateRange(startDate, endDate);
        return ResponseEntity.ok(sensorService.getAllSensorsData(startDate, endDate, pageable));
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
            LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull @PastOrPresent
            LocalDateTime endDate,
            @PageableDefault(size = 8, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable) {
        validateDateRange(startDate, endDate);
        return ResponseEntity.ok(
                sensorService.getSensorsDataByFaculty(facultyName, startDate, endDate, pageable));
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date must be before end date");
        }
    }
}
