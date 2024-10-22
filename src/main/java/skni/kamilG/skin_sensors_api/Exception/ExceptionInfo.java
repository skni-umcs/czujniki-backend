package skni.kamilG.skin_sensors_api.Exception;

import java.time.LocalDateTime;

public record ExceptionInfo(LocalDateTime errorTimestamp, String errorMessage, String request) {

  public ExceptionInfo(String errorMessage, String request) {
    this(LocalDateTime.now(), errorMessage, request);
  }
}
