package skni.kamilG.skin_sensors_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkniSensorsApiApplication {
  // redeploy
  public static void main(String[] args) {
    SpringApplication.run(SkniSensorsApiApplication.class, args);
	}

}
