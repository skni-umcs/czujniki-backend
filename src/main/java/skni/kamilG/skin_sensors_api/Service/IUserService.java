package skni.kamilG.skin_sensors_api.Service;

import skni.kamilG.skin_sensors_api.Model.Sensor;

import java.util.List;

public interface IUserService {
    List<Sensor> getFavoriteSensors(Long userId);

    void removeFavoriteSensor(Long userId, Short sensorId);

    void addFavoriteSensor(Long userId, Short sensorId);

}
