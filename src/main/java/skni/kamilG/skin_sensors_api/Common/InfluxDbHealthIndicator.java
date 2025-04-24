package skni.kamilG.skin_sensors_api.Common;

import com.influxdb.client.InfluxDBClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class InfluxDbHealthIndicator implements HealthIndicator {

  private final InfluxDBClient influxDBClient;

  public InfluxDbHealthIndicator(InfluxDBClient influxDBClient) {
    this.influxDBClient = influxDBClient;
  }

  @Override
  public Health health() {
    try {
      influxDBClient.ping();
      return Health.up().withDetail("influx", "Available").build();
    } catch (Exception e) {
      return Health.down(e).withDetail("influx", "Unavailable").build();
    }
  }
}
