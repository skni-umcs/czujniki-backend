package skni.kamilG.skin_sensors_api.Service;

import java.util.List;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.User;

public interface IUserService {
  List<Sensor> getFavoriteSensors(Long userId);

  void removeFavoriteSensor(Long userId, Short sensorId);

  void addFavoriteSensor(Long userId, Short sensorId);

  User getUserByUsername(String username);

  Long getUserIdByUsername(String username);
}
