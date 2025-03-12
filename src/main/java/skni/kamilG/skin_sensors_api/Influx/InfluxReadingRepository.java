package skni.kamilG.skin_sensors_api.Influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import java.util.List;
import java.util.Optional;
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

  public InfluxReadingRepository(InfluxDBClient influxDBClient) {
    this.influxDBClient = influxDBClient;
  }

  public Optional<InfluxReading> getLatestReading(Short sensorId, Short refreshRate) {
    String fluxQuery = buildQueryWithParams(sensorId, refreshRate);
    QueryApi queryApi = influxDBClient.getQueryApi();

    try {
      List<FluxTable> tables = queryApi.query(fluxQuery);

      if (tables.isEmpty() || tables.getFirst().getRecords().isEmpty()) {
        return Optional.empty();
      }

      FluxRecord record = tables.getFirst().getRecords().getFirst();
      return Optional.of(mapRecordToReading(record));

    } catch (DataMappingException e) {
      log.warn("Failed to map sensor data: {}", e.getMessage());
      return Optional.empty();
    } catch (Exception e) {
      log.error("Error executing InfluxDB query: {}", fluxQuery, e);
      throw new InfluxDBQueryException("Failed to fetch data from InfluxDB", e);
    }
  }

  /**
   * Builds the Flux query based on the original specification
   *
   * @return Flux query string
   */
  private String buildQueryWithParams(Short sensorId, Short refreshRate) {
    return String.format(
        """
        from(bucket: "%s")
          |> range(start: -%ds)
          |> filter(fn: (r) => r["_measurement"] == "sensor_readings")
          |> filter(fn: (r) => r["_field"] == "%s" or
                               r["_field"] == "%s" or
                               r["_field"] == "%s" or
                               r["_field"] == "%s")
          |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
          |> filter(fn: (r) => r["sensor_id"] == %s)
          |> last(column: "_time")
        """,
        bucket,
        refreshRate,
        HUMIDITY_FIELD,
        PRESSURE_FIELD,
        TEMPERATURE_FIELD,
        SENSOR_ID_FIELD,
        sensorId);
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

    try {
      reading.setTime(record.getTime());
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
