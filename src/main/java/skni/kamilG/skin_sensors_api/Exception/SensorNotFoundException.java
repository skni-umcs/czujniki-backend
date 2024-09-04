package skni.kamilG.skin_sensors_api.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import skni.kamilG.skin_sensors_api.Model.Location;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SensorNotFoundException extends RuntimeException {

  public SensorNotFoundException() {
    super("Sensor not found");
  }

  public SensorNotFoundException(Short sensorId) {
    super(String.format("Sensor not found with ID %d", sensorId));
  }

  public SensorNotFoundException(Location location) {
    super(String.format("Sensor not found in location: %s", location.getFacultyName()));
  }
}
