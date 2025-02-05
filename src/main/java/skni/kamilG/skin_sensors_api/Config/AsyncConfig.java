package skni.kamilG.skin_sensors_api.Config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean("virtualThreadExecutor")
  public Executor taskExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }
}
