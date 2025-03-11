package skni.kamilG.skin_sensors_api.Config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadConfig {

  @Bean
  public Executor virtualThreadExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  @Bean
  public ScheduledExecutorService scheduledVirtualThreadExecutor() {
    return Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors(), Thread.ofVirtual().factory());
  }
}
