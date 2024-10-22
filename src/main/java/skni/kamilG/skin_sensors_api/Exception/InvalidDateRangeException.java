package skni.kamilG.skin_sensors_api.Exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InvalidDateRangeException extends RuntimeException {

  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  public InvalidDateRangeException(String message) {
    super(message);
    this.startTime = null;
    this.endTime = null;
  }

  public InvalidDateRangeException(LocalDateTime startTime, LocalDateTime endTime) {
    super("Invalid dateTime order " + startTime + " - " + endTime);
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
