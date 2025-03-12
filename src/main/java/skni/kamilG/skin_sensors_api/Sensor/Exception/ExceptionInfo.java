package skni.kamilG.skin_sensors_api.Sensor.Exception;

import java.time.Clock;
import java.time.ZonedDateTime;

public record ExceptionInfo(ZonedDateTime errorTimestamp, String errorMessage, String request) {

  public ExceptionInfo(String errorMessage, String request, Clock clock) {
    this(ZonedDateTime.now(clock), errorMessage, request);
  }
}
