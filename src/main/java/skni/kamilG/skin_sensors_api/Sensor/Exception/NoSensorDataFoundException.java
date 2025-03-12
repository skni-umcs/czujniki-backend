package skni.kamilG.skin_sensors_api.Sensor.Exception;

import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class NoSensorDataFoundException extends RuntimeException {
  private final ZonedDateTime startTime;
  private final ZonedDateTime endTime;

  public NoSensorDataFoundException() {
    super("Sensor data not found");
    this.startTime = null;
    this.endTime = null;
  }

  public NoSensorDataFoundException(ZonedDateTime startTime, ZonedDateTime endTime) {
    super(String.format("Sensor data not found for period: %s - %s", startTime, endTime));
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
