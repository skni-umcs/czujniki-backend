package skni.kamilG.skin_sensors_api.Sensor.Model.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SensorRequest(
    @NotNull Short sensorId,
    @Min(value = 15, message = "Refresh rate must be at least 15 seconds")
        @Max(value = 600, message = "Refresh rate cannot exceed 600 seconds")
        @NotNull
        Short refreshRate) {}
