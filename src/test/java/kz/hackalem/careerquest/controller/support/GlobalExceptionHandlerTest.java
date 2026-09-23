package kz.hackalem.careerquest.controller.support;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

class GlobalExceptionHandlerTest {
  @Test
  void apiErrorsAreSanitized() {
    var response =
        (ResponseEntity<?>)
            new GlobalExceptionHandler()
                .handle(
                    new NotFoundException("private details"),
                    new MockHttpServletRequest("GET", "/api/missing"));
    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(response.getBody().toString()).doesNotContain("private details");
  }

  @Test
  void browserGetsErrorTemplate() {
    var view =
        (ModelAndView)
            new GlobalExceptionHandler()
                .handle(new BusinessException("invalid"), new MockHttpServletRequest("GET", "/"));
    assertThat(view.getViewName()).isEqualTo("error/error");
    assertThat(view.getStatus().value()).isEqualTo(400);
  }
}
