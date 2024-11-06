package skni.kamilG.skin_sensors_api.Model.Sensor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SensorStatus {
  ONLINE("Online"),
  OFFLINE("Offline"),
  ERROR("Error");

  private final String status;
}
