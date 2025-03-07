package skni.kamilG.skin_sensors_api.Sensor.Exception;

public class SensorUpdateException extends RuntimeException {
  public SensorUpdateException(String message, Throwable cause) {
    super(message, cause);
  }

  public SensorUpdateException(Throwable message) {
    super(String.format("Unexpected exception %s when updating sensorsData", message));
  }
}
