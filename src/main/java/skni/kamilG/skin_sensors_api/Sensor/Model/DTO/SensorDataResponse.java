package skni.kamilG.skin_sensors_api.Sensor.Model.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record SensorDataResponse(
    @NotNull @Valid ZonedDateTime timestamp,
    @NotNull Short temperature,
    @NotNull Integer humidity,
    @NotNull Integer pressure) {}
