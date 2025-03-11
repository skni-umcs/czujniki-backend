package skni.kamilG.skin_sensors_api.Sensor.Model.Mapper;

import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorRepository;

@NoArgsConstructor
@Mapper(
    componentModel = "spring",
    uses = {SensorRepository.class})
public abstract class SensorMapper {

  protected SensorRepository sensorRepository;

  @Autowired
  protected void setSensorRepository(SensorRepository sensorRepository) {
    this.sensorRepository = sensorRepository;
  }

  public abstract SensorResponse mapToSensorResponse(Sensor sensor);
}
