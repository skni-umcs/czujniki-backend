package skni.kamilG.skin_sensors_api.Service;


import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;

import java.time.LocalDateTime;
import java.util.List;

public interface ISensorService {

    //Main Functionalities
    Sensor getSensorById(Short sensorId);

    List<SensorData> getSensorHistoryById(Short sensorId, LocalDateTime startDate, LocalDateTime endDate);

    List<Sensor> getAllSensorsData();

    List<SensorData> getAllSensorsHistory(LocalDateTime startDate, LocalDateTime endDate);

    List<Sensor> getSensorsByFaculty(String facultyName);

    List<SensorData> getSensorsHistoryByFaculty(String facultyName, LocalDateTime startDate, LocalDateTime endDate);

    //Admin Functionalities
    Sensor addSensor(Sensor sensor);

    Sensor updateSensor(Sensor sensor);

    void deleteSensor(Short sensorId);

}


