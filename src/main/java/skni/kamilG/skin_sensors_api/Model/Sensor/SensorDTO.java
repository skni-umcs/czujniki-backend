package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import skni.kamilG.skin_sensors_api.Model.Location;

@Data
public class SensorDTO {
  @NotNull(message = "Status is required")
  private SensorStatus status;

  @Valid
  @NotNull(message = "Location is required")
  private Location location;

}
