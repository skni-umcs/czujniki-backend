package skni.kamilG.skin_sensors_api.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSensorDataFoundException.class)
  public ResponseEntity<ExceptionInfo> handleNoSensorDataFoundException(
      NoSensorDataFoundException ex, WebRequest request) {

    log.error("No sensors data found. Period: {} - {}", ex.getStartTime(), ex.getEndTime());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ExceptionInfo(ex.getMessage(), request.getDescription(true)));
  }

  @ExceptionHandler(InvalidDateRangeException.class)
  public ResponseEntity<ExceptionInfo> handleInvalidDateRange(
      InvalidDateRangeException ex, WebRequest request) {

    log.error("Invalid date range: unlogical order: {} - {}", ex.getStartTime(), ex.getEndTime());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionInfo(ex.getMessage(), request.getDescription(true)));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionInfo> handleGeneralException(Exception ex, WebRequest request) {

    log.error("Unexpected error: ", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionInfo(ex.getMessage(), request.getDescription(true)));
  }
}
