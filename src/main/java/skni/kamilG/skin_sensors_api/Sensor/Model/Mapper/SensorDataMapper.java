package skni.kamilG.skin_sensors_api.Sensor.Model.Mapper;

import org.mapstruct.Mapper;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorDataResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.SensorData;

@Mapper(componentModel = "spring")
public interface SensorDataMapper {

  SensorDataResponse createSensorDataToSensorDataResponse(SensorData sensorData);
}
