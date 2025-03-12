package skni.kamilG.skin_sensors_api.Sensor.Exception;

import java.time.Clock;
import java.time.LocalDateTime;

public record ExceptionInfo(LocalDateTime errorTimestamp, String errorMessage, String request) {

  public ExceptionInfo(String errorMessage, String request, Clock clock) {
    this(LocalDateTime.now(clock), errorMessage, request);
  }
}
