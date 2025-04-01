package skni.kamilG.skin_sensors_api.Admin.DTO;

public sealed interface SensorCommand permits RefreshRateCommand, StatusCommand {
  Short sensorId();
}
