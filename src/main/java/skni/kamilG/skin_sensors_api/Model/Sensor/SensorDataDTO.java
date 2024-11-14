package skni.kamilG.skin_sensors_api.Model.Sensor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SensorDataDTO(
    @NotNull @Valid LocalDateTime timestamp,
    @NotNull short temperature,
    @NotNull long humidity,
    @NotNull long pressure,
    @NotNull long gasResistance) {}
