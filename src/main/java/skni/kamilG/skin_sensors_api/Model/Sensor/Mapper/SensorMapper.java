package skni.kamilG.skin_sensors_api.Model.Sensor.Mapper;

import org.mapstruct.Mapper;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorRequest;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;

@Mapper(componentModel = "spring")
public interface SensorMapper {

  Sensor createSensorRequestToSensor(SensorRequest createSensorRequestDTO);

  SensorResponse createSensorToSensorResponse(Sensor sensor);
}
