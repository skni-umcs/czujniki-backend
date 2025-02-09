package skni.kamilG.skin_sensors_api.Repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import skni.kamilG.skin_sensors_api.Exception.DataMappingException;
import skni.kamilG.skin_sensors_api.Exception.InfluxDBQueryException;
import skni.kamilG.skin_sensors_api.Model.Sensor.InfluxReading;

@Slf4j
@AllArgsConstructor
@Repository
public class InfluxReadingRepository {

  private final InfluxDBClient influxDBClient;

  private static final String SENSOR_ID_FIELD = "sensor_id";
  private static final String TEMPERATURE_FIELD = "raw_temperature";
  private static final String HUMIDITY_FIELD = "raw_humidity";
  private static final String PRESSURE_FIELD = "raw_pressure";
  private static final String GAS_RESISTANCE_FIELD = "raw_gas_resistance";

  public List<InfluxReading> getLatestReadings() {
    List<InfluxReading> readings = new ArrayList<>();
    String fluxQuery =
        """
from(bucket: "czujniki")
  |> range(start: -30s)
  |> filter(fn: (r) => r["_measurement"] == "sensor_readings")
  |> filter(fn: (r) => r["_field"] == "raw_gas_resistance" or
                       r["_field"] == "raw_humidity" or
                       r["_field"] == "raw_pressure" or
                       r["_field"] == "raw_temperature" or
                       r["_field"] == "sensor_id")
  |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
            """;
    QueryApi queryApi = influxDBClient.getQueryApi();

    try {
      List<FluxTable> tables = queryApi.query(fluxQuery);
      for (FluxTable table : tables) {
        for (FluxRecord record : table.getRecords()) {
          readings.add(mapRecordToReading(record));
        }
      }
    } catch (Exception e) {
      log.error("Error executing InfluxDB query: {}", fluxQuery, e);
      throw new InfluxDBQueryException("Failed to fetch data from InfluxDB", e);
    }

    return readings;
  }

  private InfluxReading mapRecordToReading(FluxRecord record) {
    InfluxReading reading = new InfluxReading();
    reading.setTime(record.getTime());

    reading.setSensorId(getValueAsSensorId(record));
    reading.setRawTemperature(getValueAsTemperature(record));
    reading.setRawHumidity(getValueAsInteger(record, HUMIDITY_FIELD));
    reading.setRawPressure(getValueAsInteger(record, PRESSURE_FIELD));
    reading.setRawGasResistance(getValueAsInteger(record, GAS_RESISTANCE_FIELD));

    return reading;
  }

  private Short getValueAsSensorId(FluxRecord record) {
    Number value = getRequiredValue(record, InfluxReadingRepository.SENSOR_ID_FIELD);
    return value.shortValue();
  }

  private Short getValueAsTemperature(FluxRecord record) {
    Number value = getRequiredValue(record, InfluxReadingRepository.TEMPERATURE_FIELD);
    return value.shortValue();
  }

  private Integer getValueAsInteger(FluxRecord record, String field) {
    Number value = getRequiredValue(record, field);
    return value.intValue();
  }

  private Number getRequiredValue(FluxRecord record, String field) {
    Object value = record.getValueByKey(field);
    if (value == null) {
      throw new DataMappingException("Missing required field: " + field);
    }
    if (!(value instanceof Number)) {
      throw new DataMappingException("Field is not a number: " + field);
    }
    return (Number) value;
  }
}
