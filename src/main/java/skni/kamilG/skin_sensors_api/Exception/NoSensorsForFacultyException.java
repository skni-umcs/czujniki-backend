package skni.kamilG.skin_sensors_api.Exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NoSensorsForFacultyException extends Throwable {
  public NoSensorsForFacultyException(@NotNull @NotBlank @NotNull @NotBlank String facultyName) {}
}
