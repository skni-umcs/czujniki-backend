package skni.kamilG.skin_sensors_api.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.SensorData;
import skni.kamilG.skin_sensors_api.Model.SensorStatus;
import skni.kamilG.skin_sensors_api.Repository.SensorDataRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorRepository;
import skni.kamilG.skin_sensors_api.Repository.SensorStatusRepository;

import java.util.Optional;

@Service
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private SensorStatusRepository sensorStatusRepository;

    @Scheduled(fixedRate = 60000)
    public void updateSensorValues() {

        Iterable<Sensor> sensors = sensorRepository.findAll();

        for (Sensor sensor : sensors) {

            Optional<SensorData> latestData = sensorDataRepository.findTopBySensorOrderByTimestampDesc(sensor);

            if (latestData.isPresent()) {
                SensorData data = latestData.get();

                sensor.updateCurrentValues(data);
                sensorRepository.save(sensor);
                SensorStatus status = new SensorStatus();
                status.setSensorId(sensor.getSensorId());
                status.setStatus("Active");
                status.setLastChecked(ZonedDateTime.now());
                sensorStatusRepository.save(status);
            } else {
                // Opcjonalnie: Zaktualizuj status sensora na "Offline" jeśli brak danych
                SensorStatus status = new SensorStatus();
                status.setSensorId(sensor.getSensorId());
                status.setStatus("Offline");
                status.setLastChecked(ZonedDateTime.now());
                sensorStatusRepository.save(status);
            }
        }
    }

    // Metoda do zapisywania danych sensora
    public SensorData saveSensorData(SensorData sensorData) {
        Sensor sensor = sensorData.getSensor();
        sensor.updateCurrentValues(sensorData);
        sensorRepository.save(sensor); // Zapisuje również zaktualizowane wartości
        return sensorDataRepository.save(sensorData);
    }

    // Metoda do pobierania aktualnych wartości czujnika
    public Sensor getCurrentSensorValues(Short sensorId) {
        return sensorRepository.findById(sensorId).orElseThrow(() -> new RuntimeException("Sensor not found"));
    }

    // Metoda do pobierania historii danych czujnika
    public Iterable<SensorData> getSensorDataHistory(Short sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(() -> new RuntimeException("Sensor not found"));
        return sensorDataRepository.findBySensor(sensor);
    }
}
