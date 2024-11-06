package skni.kamilG.skin_sensors_api.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI userApiDoc() {
    return new OpenAPI().info(new Info().title("Rest Sensors API").version("0.1"));
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder().group("public").pathsToMatch("/api/users/me").build();
  }

  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder().group("users").pathsToMatch("/api/sensors").build();
  }
}
