package skni.kamilG.skin_sensors_api.Model.Sensor;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SensorDataMapper {
  @Mapping(source = "timestamp", target = "timestamp")
  @Mapping(source = "temperature", target = "temperature")
  @Mapping(source = "humidity", target = "humidity")
  @Mapping(source = "pressure", target = "pressure")
  @Mapping(source = "gasResistance", target = "gasResistance")
  SensorData createSensorDataDtoToSensorData(SensorDataDTO sensorDataDTO);

  @InheritInverseConfiguration
  SensorDataDTO createSensorDataToSensorDataDTO(SensorData sensorData);
}
