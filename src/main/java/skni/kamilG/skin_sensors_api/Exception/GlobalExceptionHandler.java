package skni.kamilG.skin_sensors_api.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSensorDataFoundException.class)
  public ResponseEntity<ExceptionInfo> handleNoSensorDataFoundException(
      NoSensorDataFoundException ex, WebRequest request) {
    ExceptionInfo info =
        new ExceptionInfo(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(info, HttpStatus.NO_CONTENT);
  }
}
