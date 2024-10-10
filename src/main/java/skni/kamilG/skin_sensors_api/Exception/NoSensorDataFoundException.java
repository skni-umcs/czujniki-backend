package skni.kamilG.skin_sensors_api.Exception;

import java.time.LocalDateTime;

public class NoSensorDataFoundException extends RuntimeException {

  public NoSensorDataFoundException() {
    super("Sensor data not found");
  }

  public NoSensorDataFoundException(LocalDateTime startTime, LocalDateTime endTime) {
    super("Sensor data not found at " + startTime + " - " + endTime);
  }
}
