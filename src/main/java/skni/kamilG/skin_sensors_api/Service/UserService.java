package skni.kamilG.skin_sensors_api.Service;

import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Exception.ResourceNotFoundException;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.User;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;
import skni.kamilG.skin_sensors_api.Repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found with id " + sensorId));

        Set<Sensor> favoriteSensors = user.getFavoriteSensors();
        if (favoriteSensors == null) {
            favoriteSensors = new HashSet<>();
        }

        favoriteSensors.add(sensor);
        user.setFavoriteSensors(favoriteSensors);
        userRepository.save(user);
    }
    @Override
    public void removeFavoriteSensor(Long userId, Short sensorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found with id " + sensorId));

        Set<Sensor> favoriteSensors = user.getFavoriteSensors();
        if (favoriteSensors != null) {
            favoriteSensors.remove(sensor);
            user.setFavoriteSensors(favoriteSensors);
            userRepository.save(user);
        }
    }
    @Override
    public List<Sensor> getFavoriteSensors(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        return new ArrayList<>(user.getFavoriteSensors());
    }
}
