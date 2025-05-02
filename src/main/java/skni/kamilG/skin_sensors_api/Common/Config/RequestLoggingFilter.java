//package skni.kamilG.skin_sensors_api.Common.Config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
///**
// * Simple request logging filter that captures client IP, User-Agent, and Referer for all incoming
// * HTTP requests.
// */
//@Component
//@Slf4j
//public class RequestLoggingFilter extends OncePerRequestFilter {
//
//  private static final List<String> IP_HEADERS =
//      Collections.unmodifiableList(
//          Arrays.asList(
//              "X-Forwarded-For",
//              "Proxy-Client-IP",
//              "WL-Proxy-Client-IP",
//              "HTTP_X_FORWARDED_FOR",
//              "HTTP_X_FORWARDED",
//              "HTTP_X_CLUSTER_CLIENT_IP",
//              "HTTP_CLIENT_IP",
//              "HTTP_FORWARDED_FOR",
//              "HTTP_FORWARDED",
//              "HTTP_VIA",
//              "REMOTE_ADDR"));
//
//  @Override
//  protected void doFilterInternal(
//      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//      throws ServletException, IOException {
//
//    String clientIp = extractClientIpAddress(request);
//    String userAgent = request.getHeader("User-Agent");
//    String referer = request.getHeader("Referer");
//    String requestMethod = request.getMethod();
//    String requestUri = request.getRequestURI();
//
//    try {
//      filterChain.doFilter(request, response);
//    } finally {
//      int status = response.getStatus();
//      log.info(
//          "Request: [{}] {} | IP: {} | User-Agent: {} | Referer: {} | Status: {}",
//          requestMethod,
//          requestUri,
//          clientIp,
//          userAgent != null ? userAgent : "Not provided",
//          referer != null ? referer : "Not provided",
//          status);
//    }
//  }
//
//  @Override
//  protected boolean shouldNotFilter(HttpServletRequest request) {
//    String requestUri = request.getRequestURI();
//    return requestUri.startsWith("/actuator");
//  }
//
//  /**
//   * Extracts the client IP address from the request, checking various headers to handle proxies and
//   * load balancers.
//   *
//   * @param request The HTTP request
//   * @return The client IP address
//   */
//  private String extractClientIpAddress(HttpServletRequest request) {
//    return IP_HEADERS.stream()
//        .map(request::getHeader)
//        .filter(
//            header -> header != null && !header.isEmpty() && !"unknown".equalsIgnoreCase(header))
//        .map(header -> header.split(",")[0].trim())
//        .findFirst()
//        .orElse(request.getRemoteAddr());
//  }
//}
