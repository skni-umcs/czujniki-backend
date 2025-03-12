package skni.kamilG.skin_sensors_api.Sensor.Exception;

import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class InvalidDateRangeException extends RuntimeException {

  private final ZonedDateTime startTime;
  private final ZonedDateTime endTime;

  public InvalidDateRangeException(String message) {
    super(message);
    this.startTime = null;
    this.endTime = null;
  }

  public InvalidDateRangeException(ZonedDateTime startTime, ZonedDateTime endTime) {
    super("Invalid dateTime order " + startTime + " - " + endTime);
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
