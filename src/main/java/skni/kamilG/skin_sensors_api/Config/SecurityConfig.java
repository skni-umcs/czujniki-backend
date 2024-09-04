package skni.kamilG.skin_sensors_api.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/user/**")
                    .hasRole("USER")
                    .anyRequest()
                    .authenticated())
        .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
        .logout(LogoutConfigurer::permitAll);

    return http.build();
    // TODO przejscie na JWT w pozniejszym etapie projektu
  }
}
