package skni.kamilG.skin_sensors_api.Influx.Exception;

public class InfluxDBQueryException extends RuntimeException {
  public InfluxDBQueryException(String message, Throwable cause) {
    super(message, cause);
  }
}
