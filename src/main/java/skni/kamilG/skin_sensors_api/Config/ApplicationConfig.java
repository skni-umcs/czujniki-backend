package skni.kamilG.skin_sensors_api.Config;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;
import skni.kamilG.skin_sensors_api.Sensor.Model.DTO.SensorResponse;

@Configuration
public class ApplicationConfig {
  @Value("${app.timezone:UTC}")
  private String timezone;

  @Bean
  public Clock clock() {
    return Clock.system(ZoneId.of(timezone));
  }

  @Bean
  public Sinks.Many<SensorResponse> sensorUpdatesSink() {
    return Sinks.many().replay().limit(1);
  }

  @Bean
  public Duration heartbeatInterval() {
    return Duration.ofSeconds(25);
  }
}
