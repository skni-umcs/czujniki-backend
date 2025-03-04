package skni.kamilG.skin_sensors_api.Config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;

@Configuration
public class ApplicationConfig {
  @Value("${app.timezone:Europe/Warsaw}")
  private String timezone;

  @Bean
  public Clock clock() {
    return Clock.system(ZoneId.of(timezone));
  }

  @Bean
  public Sinks.Many<SensorResponse> sensorUpdatesSink() {
    return Sinks.many().replay().limit(20);
  }
}
