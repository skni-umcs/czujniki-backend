package skni.kamilG.skin_sensors_api.Common.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${cors.frontend.dev.url}")
  private String frontendDevUrl;

  @Value("${cors.frontend.prd.url}")
  private String frontendProdUrl;

  @Value("${cors.admin.url}")
  private String adminUrl;

  @Value("${cors.max-age:3600}")
  private long maxAge;

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    String[] frontendUrls = {frontendDevUrl, frontendProdUrl};

    registry
        .addMapping("/api/**")
        .allowedOrigins(frontendUrls)
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(maxAge);

    registry
        .addMapping("/live-api/**")
        .allowedOrigins(frontendUrls)
        .allowedMethods("GET")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(maxAge);

    registry
        .addMapping("/admin/**")
        .allowedOrigins(adminUrl)
        .allowedMethods("PATCH")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(maxAge);
  }
}
