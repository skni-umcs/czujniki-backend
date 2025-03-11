package skni.kamilG.skin_sensors_api.Sensor.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import skni.kamilG.skin_sensors_api.Sensor.Model.Location;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SensorNotFoundException extends RuntimeException {

  public SensorNotFoundException(Short sensorId) {
    super(String.format("Sensor not found with ID %d", sensorId));
  }

  public SensorNotFoundException(Location location) {
    super(String.format("Sensor not found in location: %s", location.getFacultyName()));
  }
}
