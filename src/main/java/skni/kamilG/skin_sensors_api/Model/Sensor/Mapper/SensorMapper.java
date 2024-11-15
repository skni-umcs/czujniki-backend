package skni.kamilG.skin_sensors_api.Model.Sensor.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorRequest;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;

@Mapper(componentModel = "spring")
public interface SensorMapper {

  Sensor createSensorRequestToSensor(SensorRequest createSensorRequestDTO);

  @Mapping(source = "currentTemperature", target = "temperature")
  @Mapping(source = "currentHumidity", target = "humidity")
  @Mapping(source = "currentPressure", target = "pressure")
  @Mapping(source = "currentGasResistance", target = "gasResistance")
  SensorResponse createSensorToSensorResponse(Sensor sensor);
}
