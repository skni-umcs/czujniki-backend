package skni.kamilG.skin_sensors_api.Model.Sensor.Mapper;

import org.mapstruct.Mapper;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorDataResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.SensorData;

@Mapper(componentModel = "spring")
public interface SensorDataMapper {

  SensorDataResponse createSensorDataToSensorDataResponse(SensorData sensorData);
}
