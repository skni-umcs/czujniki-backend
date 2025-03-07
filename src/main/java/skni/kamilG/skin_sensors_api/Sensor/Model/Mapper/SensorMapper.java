package skni.kamilG.skin_sensors_api.Sensor.Model.Mapper;

import org.mapstruct.Mapper;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorRequest;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;

@Mapper(componentModel = "spring")
public interface SensorMapper {

  Sensor createSensorRequestToSensor(SensorRequest createSensorRequestDTO);

  SensorResponse createSensorToSensorResponse(Sensor sensor);
}
