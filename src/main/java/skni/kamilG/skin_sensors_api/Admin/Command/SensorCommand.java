package skni.kamilG.skin_sensors_api.Admin.Command;

public sealed interface SensorCommand permits RefreshRateCommand, StatusCommand {
  Short sensorId();
}
