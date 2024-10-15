package skni.kamilG.skin_sensors_api.Exception;

import java.time.LocalDateTime;

public record ExceptionInfo(
    LocalDateTime errorTimestamp, int httpStatus, String errorMessage, String request) {}
