package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import skni.kamilG.skin_sensors_api.Model.Location;

public record SensorDTO(@NotNull SensorStatus status, @Valid @NotNull Location location) {}
