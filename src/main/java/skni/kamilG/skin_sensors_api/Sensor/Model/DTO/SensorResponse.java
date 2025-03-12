package skni.kamilG.skin_sensors_api.Sensor.Model.DTO;

import java.time.ZonedDateTime;
import skni.kamilG.skin_sensors_api.Sensor.Model.Location;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorStatus;

public record SensorResponse(
    Short id,
    SensorStatus status,
    ZonedDateTime lastUpdate,
    Short temperature,
    Integer humidity,
    Integer pressure,
    Location location,
    Short floor) {}
