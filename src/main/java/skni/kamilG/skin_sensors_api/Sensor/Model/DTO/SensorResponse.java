package skni.kamilG.skin_sensors_api.Sensor.Model.DTO;

import java.time.LocalDateTime;
import skni.kamilG.skin_sensors_api.Sensor.Model.Location;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorStatus;

public record SensorResponse(
    Short id,
    SensorStatus status,
    LocalDateTime lastUpdate,
    Short temperature,
    Integer humidity,
    Integer pressure,
    Location location) {}
