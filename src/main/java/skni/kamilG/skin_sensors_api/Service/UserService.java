package skni.kamilG.skin_sensors_api.Service;

import java.util.*;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Exception.UserNotFoundException;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.User;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;
import skni.kamilG.skin_sensors_api.Repository.UserRepository;

@Service
public class UserService implements IUserService {

  private final UserRepository userRepository;

  private final SensorRepository sensorRepository;

  public UserService(UserRepository userRepository, SensorRepository sensorRepository) {
    this.userRepository = userRepository;
    this.sensorRepository = sensorRepository;
  }

  @Override
  public void addFavoriteSensor(Long userId, Short sensorId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    Sensor sensor =
        sensorRepository
            .findById(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(sensorId));

    Set<Sensor> favoriteSensors = user.getFavoriteSensors();
    if (favoriteSensors == null) {
      favoriteSensors = new HashSet<>();
    }

    favoriteSensors.add(sensor);
    user.setFavoriteSensors(favoriteSensors);
    userRepository.save(user);
  }

  @Override
  public User getUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));
  }

  @Override
  public void removeFavoriteSensor(Long userId, Short sensorId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    Sensor sensor =
        sensorRepository
            .findById(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(sensorId));

    Set<Sensor> favoriteSensors = user.getFavoriteSensors();
    if (favoriteSensors != null) {
      favoriteSensors.remove(sensor);
      user.setFavoriteSensors(favoriteSensors);
      userRepository.save(user);
    }
  }

  @Override
  public List<Sensor> getFavoriteSensors(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    return new ArrayList<>(user.getFavoriteSensors());
  }
}
