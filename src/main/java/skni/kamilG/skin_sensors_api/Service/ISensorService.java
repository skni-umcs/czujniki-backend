package skni.kamilG.skin_sensors_api.Service;

import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;
import skni.kamilG.skin_sensors_api.Model.SensorStatus;

import java.util.List;

public interface ISensorService {

    List<Sensor> getAllSensors();

    Sensor getSensorById(Long id);

    Sensor addSensor(Sensor sensor);

    Sensor updateSensor(Long id, Sensor sensor);

    void deleteSensor(Long id);

    List<Sensor> getSensorsByDepartment(String department);

    SensorData getSensorData(Short id);

    void reportSensorFailure(Long id, String issueDescription);

    SensorStatus checkSensorStatus(Long id);

    void updateSensorLocation(Long id, String newLocation);
}

