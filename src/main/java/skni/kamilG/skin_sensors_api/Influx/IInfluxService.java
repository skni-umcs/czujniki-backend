package skni.kamilG.skin_sensors_api.Influx;

import java.util.Optional;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorData;

public interface IInfluxService {
  Optional<SensorData> fetchLatestData(Short sensorId, Short refreshRate);
}
