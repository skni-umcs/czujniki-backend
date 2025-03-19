package skni.kamilG.skin_sensors_api.Admin.Command;

import jakarta.validation.constraints.NotNull;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorStatus;

public record StatusCommand(@NotNull Short sensorId, @NotNull SensorStatus status)
    implements SensorCommand {}
