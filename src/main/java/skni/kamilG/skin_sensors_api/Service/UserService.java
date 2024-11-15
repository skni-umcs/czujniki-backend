package skni.kamilG.skin_sensors_api.Service;

import jakarta.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skni.kamilG.skin_sensors_api.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Exception.UserNotFoundException;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.Mapper.SensorMapper;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;
import skni.kamilG.skin_sensors_api.Model.User;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;
import skni.kamilG.skin_sensors_api.Repository.UserRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService implements IUserService {

  private final UserRepository userRepository;
  private final SensorRepository sensorRepository;
  private final SensorMapper sensorMapper;

  public UserService(
      UserRepository userRepository, SensorRepository sensorRepository, SensorMapper sensorMapper) {
    this.userRepository = userRepository;
    this.sensorRepository = sensorRepository;
    this.sensorMapper = sensorMapper;
  }

  @Transactional
  @Override
  public void addFavoriteSensor(@NotNull Long userId, @NotNull Short sensorId) {

    log.warn("Adding favorite sensor {} for user {}", sensorId, userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    Sensor sensor =
        sensorRepository
            .findById(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(sensorId));

    user.getFavoriteSensors().add(sensor);
    log.info("Successfully added favorite sensor {} for user {}", sensorId, userId);
  }

  @Transactional
  @Override
  public void removeFavoriteSensor(@NotNull Long userId, @NotNull Short sensorId) {

    log.warn("Removing favorite sensor {} for user {}", sensorId, userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    Sensor sensor =
        sensorRepository
            .findById(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(sensorId));

    user.getFavoriteSensors().remove(sensor);
    log.info("Successfully removed favorite sensor {} for user {}", sensorId, userId);
  }

  @Override
  public User getUserByUsername(String username) {

    log.warn("Getting user by username {}", username);

    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));
  }

  @Override
  public Long getUserIdByUsername(String username) {

    log.warn("Getting userID by username {}", username);

    return userRepository
        .findUserIdByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  @Override
  public List<SensorResponse> getFavoriteSensors(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    return user.getFavoriteSensors().stream()
        .map(sensorMapper::createSensorToSensorResponse)
        .collect(Collectors.toList());
  }
}
