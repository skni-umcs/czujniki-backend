package skni.kamilG.skin_sensors_api.Common.Config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Filter that checks for the API key in the X-API-KEY header. This is a simple authentication
 * mechanism for service-to-service communication.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ApiKeyFilter implements Filter {

  @Value("${api.key}")
  private String apiKey;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String requestPath = httpRequest.getRequestURI();

    if (requestPath.startsWith("/api/sensor/refresh-rates")) {
      String providedApiKey = httpRequest.getHeader("X-API-KEY");

      if (!apiKey.equals(providedApiKey)) {
        log.warn("Unauthorized access attempt to {} from {}", requestPath, request.getRemoteAddr());
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }

    chain.doFilter(request, response);
  }
}
