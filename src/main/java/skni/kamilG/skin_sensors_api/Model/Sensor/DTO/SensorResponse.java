package skni.kamilG.skin_sensors_api.Model.Sensor.DTO;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.validation.annotation.Validated;
import skni.kamilG.skin_sensors_api.Model.Location;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorStatus;

public record SensorResponse(
    @NotNull Short id, // TODO verify if its not leak
    @Validated SensorStatus status,
    @NotNull LocalDateTime latestDateUpdate,
    @NotNull Short temperature,
    @NotNull Integer humidity,
    @NotNull Integer pressure,
    @NotNull Integer gasResistance,
    @Validated Location location) {}
