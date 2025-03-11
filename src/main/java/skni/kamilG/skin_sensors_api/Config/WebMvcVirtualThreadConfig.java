package skni.kamilG.skin_sensors_api.Config;

import java.util.concurrent.Executors;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcVirtualThreadConfig implements WebMvcConfigurer {

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    AsyncTaskExecutor virtualThreadExecutor =
        new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());

    configurer.setTaskExecutor(virtualThreadExecutor);
    configurer.setDefaultTimeout(30000);
  }
}
