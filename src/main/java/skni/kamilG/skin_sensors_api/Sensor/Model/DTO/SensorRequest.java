package skni.kamilG.skin_sensors_api.Sensor.Model.DTO;

import jakarta.validation.constraints.NotNull;
import skni.kamilG.skin_sensors_api.Sensor.Model.Location;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorStatus;

public record SensorRequest(@NotNull SensorStatus status, @NotNull Location location) {}
