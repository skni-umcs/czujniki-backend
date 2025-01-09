package skni.kamilG.skin_sensors_api.Model.Sensor.DTO;

import jakarta.validation.constraints.NotNull;
import skni.kamilG.skin_sensors_api.Model.Location;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorStatus;

public record SensorRequest(@NotNull SensorStatus status, @NotNull Location location) {}
