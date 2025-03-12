package skni.kamilG.skin_sensors_api.Sensor.Exception;

import java.io.IOException;
import java.time.Clock;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  private Clock clock;

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
      log.debug("Klient zamknął połączenie (Broken pipe)");
      return ResponseEntity.noContent().build();
    }

    log.error("Błąd IO: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @ExceptionHandler(ClientAbortException.class)
  public ResponseEntity<Void> handleClientAbortException(ClientAbortException ex) {
    log.debug("Klient przerwał połączenie: {}", ex.getMessage());
    return ResponseEntity.noContent().build();
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
}
