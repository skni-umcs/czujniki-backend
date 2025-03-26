package skni.kamilG.skin_sensors_api.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

  private String getClientIp(HttpServletRequest request) {
    String xForwardedForHeader = request.getHeader("X-Forwarded-For");
    if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
      return xForwardedForHeader.split(",")[0].trim();
    }
    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }
    return request.getRemoteAddr();
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String clientIp = getClientIp(request);
    String method = request.getMethod();
    String uri = request.getRequestURI();
    String queryString = request.getQueryString();
    String userAgent = request.getHeader("User-Agent");
    String referer = request.getHeader("Referer");

    StringBuilder fullUrl = new StringBuilder(uri);
    if (queryString != null) {
      fullUrl.append("?").append(queryString);
    }

    log.info(
        "Request: IP={}, Method={}, URL={}, UserAgent={}, Referer={}",
        clientIp,
        method,
        fullUrl,
        userAgent,
        referer);
    filterChain.doFilter(request, response);
    log.info(
        "Response: IP={}, Method={}, URL={}, Status={}",
        clientIp,
        method,
        fullUrl,
        response.getStatus());
  }
}
