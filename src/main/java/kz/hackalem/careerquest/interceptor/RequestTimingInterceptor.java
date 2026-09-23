package kz.hackalem.careerquest.interceptor;

import jakarta.servlet.http.*;
import org.slf4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestTimingInterceptor implements HandlerInterceptor {
  private static final Logger LOG = LoggerFactory.getLogger(RequestTimingInterceptor.class);

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    request.setAttribute("startedAt", System.nanoTime());
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
    Object start = req.getAttribute("startedAt");
    if (start instanceof Long time)
      LOG.info(
          "{} {} status={} elapsedMs={}",
          req.getMethod(),
          req.getRequestURI(),
          res.getStatus(),
          (System.nanoTime() - time) / 1_000_000);
  }
}
