package skni.kamilG.skin_sensors_api.Exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoSensorDataFoundException extends RuntimeException {
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  public NoSensorDataFoundException() {
    super("Sensor data not found");
    this.startTime = null;
    this.endTime = null;
  }

  public NoSensorDataFoundException(LocalDateTime startTime, LocalDateTime endTime) {
    super(String.format("Sensor data not found for period: %s - %s", startTime, endTime));
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
