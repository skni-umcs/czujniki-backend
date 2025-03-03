package skni.kamilG.skin_sensors_api.Model.Sensor.DTO;

import java.time.LocalDateTime;
import skni.kamilG.skin_sensors_api.Model.Location;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorStatus;

public record SensorResponse(
    Short id,
    SensorStatus status,
    LocalDateTime lastUpdate,
    Short temperature,
    Integer humidity,
    Integer pressure,
    Location location) {}
