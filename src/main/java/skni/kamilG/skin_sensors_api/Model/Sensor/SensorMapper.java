package skni.kamilG.skin_sensors_api.Model.Sensor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SensorMapper {
    @Mapping(source = "status", target = "status")
    @Mapping(source = "location", target = "location")
    Sensor createSensorDtoToSensor(SensorDTO createSensorDTO);
}
