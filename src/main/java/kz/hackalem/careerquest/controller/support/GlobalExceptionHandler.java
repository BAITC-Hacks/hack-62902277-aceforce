package kz.hackalem.careerquest.controller.support;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import kz.hackalem.careerquest.exception.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  public Object handle(Exception exception, HttpServletRequest request) {
    HttpStatus status =
        exception instanceof NotFoundException
            ? HttpStatus.NOT_FOUND
            : exception instanceof AccessDeniedException
                ? HttpStatus.FORBIDDEN
                : exception instanceof BusinessException
                        || exception instanceof MethodArgumentNotValidException
                        || exception instanceof IllegalArgumentException
                    ? HttpStatus.BAD_REQUEST
                    : HttpStatus.INTERNAL_SERVER_ERROR;
    if (status.is5xxServerError())
      LOG.error("Request failed: {}", request.getRequestURI(), exception);
    String key = "error." + status.value();
    if (request.getRequestURI().startsWith("/api/"))
      return ResponseEntity.status(status).body(Map.of("status", status.value(), "error", key));
    var view = new ModelAndView("error/error");
    view.setStatus(status);
    view.addObject("status", status.value());
    view.addObject("messageKey", key);
    return view;
  }
}
