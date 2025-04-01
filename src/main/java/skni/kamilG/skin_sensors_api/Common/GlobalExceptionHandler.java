package skni.kamilG.skin_sensors_api.Common;

import java.io.IOException;
import java.time.Clock;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import skni.kamilG.skin_sensors_api.LiveData.Exception.SseConnectionException;
import skni.kamilG.skin_sensors_api.Sensor.Exception.InvalidDateRangeException;
import skni.kamilG.skin_sensors_api.Sensor.Exception.NoSensorDataFoundException;
import skni.kamilG.skin_sensors_api.Sensor.Exception.SensorNotFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final Clock clock;

  @ExceptionHandler(NoSensorDataFoundException.class)
  public ResponseEntity<ExceptionInfo> handleNoSensorDataFoundException(
      NoSensorDataFoundException ex, WebRequest request) {

    log.error("No sensors data found. Period: {} - {}", ex.getStartTime(), ex.getEndTime());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ExceptionInfo(ex.getMessage(), request.getDescription(true), clock));
  }

  @ExceptionHandler(InvalidDateRangeException.class)
  public ResponseEntity<ExceptionInfo> handleInvalidDateRange(
      InvalidDateRangeException ex, WebRequest request) {

    log.error("Invalid date range: unlogical order: {} - {}", ex.getStartTime(), ex.getEndTime());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionInfo(ex.getMessage(), request.getDescription(true), clock));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionInfo> handleGeneralException(Exception ex, WebRequest request) {

    log.error("Unexpected error: ", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionInfo(ex.getMessage(), request.getDescription(true), clock));
  }

  @ExceptionHandler(SensorNotFoundException.class)
  public ResponseEntity<ExceptionInfo> handleSensorUpdateException(
      Exception ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionInfo(ex.getMessage(), request.getDescription(true), clock));
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<Void> handleIOException(IOException ex) {
    if (ex.getMessage().contains("Broken pipe")) {
      log.debug("Client closed connection (Broken pipe)");
      return ResponseEntity.noContent().build();
    }

    log.error("Error IO: ", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionInfo> handleValidationErrors(
      MethodArgumentNotValidException ex, WebRequest request) {
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));

    log.error("Validation error: {}", errorMessage);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionInfo(errorMessage, request.getDescription(false), clock));
  }

  @ExceptionHandler(AsyncRequestTimeoutException.class)
  public ResponseEntity<SseEmitter> handleAsyncRequestTimeoutException(
      AsyncRequestTimeoutException ex) {

    log.info("SSE connection timeout: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @ExceptionHandler(SseConnectionException.class)
  public ResponseEntity<ExceptionInfo> handleSseConnectionException(
      SseConnectionException ex, WebRequest request) {

    log.error("SSE connection error: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(new ExceptionInfo(ex.getMessage(), request.getDescription(false), clock));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ExceptionInfo> handleNoResourceFoundException(WebRequest request) {

    log.warn("Attempted to access non-existent resource: {}", request.getDescription(false));

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ExceptionInfo("Resource not found", request.getDescription(false), clock));
  }
}
