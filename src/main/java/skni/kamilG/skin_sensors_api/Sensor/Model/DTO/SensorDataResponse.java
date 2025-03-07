package skni.kamilG.skin_sensors_api.Sensor.Model.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SensorDataResponse(
    @NotNull @Valid LocalDateTime timestamp,
    @NotNull Short temperature,
    @NotNull Integer humidity,
    @NotNull Integer pressure) {}
