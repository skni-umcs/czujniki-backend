package skni.kamilG.skin_sensors_api.Model.Sensor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SensorMapper {
    @Mapping(source = "status", target = "status")
    @Mapping(source = "location", target = "location")
    Sensor createSensorDtoToSensor(SensorDTO createSensorDTO);
}
