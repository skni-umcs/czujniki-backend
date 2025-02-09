package skni.kamilG.skin_sensors_api.Exception;

public class InfluxProcessingException extends RuntimeException {
  public InfluxProcessingException(String message, Exception e) {
    super(message + e.getMessage());
  }
}
