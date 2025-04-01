package skni.kamilG.skin_sensors_api.Sensor.Exception;

public class SseConnectionException extends RuntimeException {
  private static final String DEFAULT_MESSAGE = "Failed to establish SSE connection";

  public SseConnectionException(Throwable cause) {
    super(DEFAULT_MESSAGE, cause);
  }

  public SseConnectionException(String additionalInfo, Throwable cause) {
    super(DEFAULT_MESSAGE + ": " + additionalInfo, cause);
  }
}
