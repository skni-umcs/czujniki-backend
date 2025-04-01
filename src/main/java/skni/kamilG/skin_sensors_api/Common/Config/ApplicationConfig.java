package skni.kamilG.skin_sensors_api.Common.Config;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ApplicationConfig {

  @Value("${app.timezone:UTC}")
  private String timezone;

  @Bean
  public Clock clock() {
    return Clock.system(ZoneId.of(timezone));
  }

  @Bean
  public Duration heartbeatInterval() {
    return Duration.ofSeconds(15);
  }
}
