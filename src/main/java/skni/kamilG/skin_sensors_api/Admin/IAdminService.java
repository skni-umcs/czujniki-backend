package skni.kamilG.skin_sensors_api.Admin;

import java.util.List;
import skni.kamilG.skin_sensors_api.Admin.Command.SensorCommand;

public interface IAdminService {
  <T extends SensorCommand> void updateSensors(List<T> commands);
}
