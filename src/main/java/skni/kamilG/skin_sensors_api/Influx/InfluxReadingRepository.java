package skni.kamilG.skin_sensors_api.Influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import skni.kamilG.skin_sensors_api.Sensor.Exception.DataMappingException;

@Slf4j
@Repository
public class InfluxReadingRepository {

  private final InfluxDBClient influxDBClient;

  private static final String TEMPERATURE_FIELD = "temperature";
  private static final String HUMIDITY_FIELD = "humidity";
  private static final String PRESSURE_FIELD = "pressure";

  private static final String SENSOR_ID_FIELD = "sensor_id";
  @Value("${influxdb.bucket:czujniki}")
  private String bucket;
  @Value("${influxdb.timeRange:60s}")
  private String timeRange;
  public InfluxReadingRepository(InfluxDBClient influxDBClient) {
    this.influxDBClient = influxDBClient;
  }

  /**
   * Gets the readings from InfluxDB for the specified time range
   *
   * @return List of sensor readings
   * @throws InfluxDBQueryException if there's an error executing the query
   */
  public List<InfluxReading> getLatestReadings() {
    List<InfluxReading> readings = new ArrayList<>();
    String fluxQuery = buildQuery();
    QueryApi queryApi = influxDBClient.getQueryApi();

    try {
      log.debug("Executing InfluxDB query: {}", fluxQuery);
      List<FluxTable> tables = queryApi.query(fluxQuery);

      for (FluxTable table : tables) {
        for (FluxRecord record : table.getRecords()) {
          try {
            readings.add(mapRecordToReading(record));
          } catch (DataMappingException e) {
            log.warn("Skipping record due to mapping error: {}", e.getMessage());
          }
        }
      }

      log.debug("Retrieved {} readings from InfluxDB", readings.size());
    } catch (Exception e) {
      log.error("Error executing InfluxDB query: {}", fluxQuery, e);
      throw new InfluxDBQueryException("Failed to fetch data from InfluxDB", e);
    }

    return readings;
  }

  /**
   * Builds the Flux query based on the original specification
   *
   * @return Flux query string
   */
  private String buildQuery() {
    return String.format(
        """
        from(bucket: "%s")
          |> range(start: -%s)
          |> filter(fn: (r) => r["_measurement"] == "sensor_readings")
          |> filter(fn: (r) => r["_field"] == "%s" or
                               r["_field"] == "%s" or
                               r["_field"] == "%s" or
                               r["_field"] == "%s")
          |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
        """,
        bucket, timeRange, HUMIDITY_FIELD, PRESSURE_FIELD, TEMPERATURE_FIELD, SENSOR_ID_FIELD);
  }

  /**
   * Maps a FluxRecord to an InfluxReading object
   *
   * @param record The FluxRecord to map
   * @return An InfluxReading object
   * @throws DataMappingException if required fields are missing or invalid
   */
  private InfluxReading mapRecordToReading(FluxRecord record) {
    InfluxReading reading = new InfluxReading();
    reading.setTime(record.getTime());

    try {
      reading.setSensorId(getValueAsSensorId(record));
      reading.setTemperature(getValueAsTemperature(record));
      reading.setHumidity(getValueAsInteger(record, HUMIDITY_FIELD));
      reading.setPressure(getValueAsInteger(record, PRESSURE_FIELD));
    } catch (Exception e) {
      throw new DataMappingException("Error mapping record: " + e.getMessage());
    }

    return reading;
  }

  private Short getValueAsSensorId(FluxRecord record) {
    Number value = getRequiredValue(record, SENSOR_ID_FIELD);
    return value.shortValue();
  }

  private Short getValueAsTemperature(FluxRecord record) {
    Number value = getRequiredValue(record, TEMPERATURE_FIELD);
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
      throw new DataMappingException(
          "Field is not a number: " + field + ", actual value: " + value);
    }
    return (Number) value;
  }
}
