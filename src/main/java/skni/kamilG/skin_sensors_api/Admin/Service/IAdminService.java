package skni.kamilG.skin_sensors_api.Admin.Service;

import java.util.List;
import skni.kamilG.skin_sensors_api.Admin.DTO.SensorCommand;

public interface IAdminService {
  <T extends SensorCommand> void updateSensors(List<T> commands);
}
