package kz.hackalem.careerquest.interceptor;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;

class RequestTimingInterceptorTest {
  @Test
  void measuresWithoutLoggingSecrets() {
    var interceptor = new RequestTimingInterceptor();
    var req = new MockHttpServletRequest("GET", "/dashboard");
    var res = new MockHttpServletResponse();
    assertThat(interceptor.preHandle(req, res, new Object())).isTrue();
    assertThat(req.getAttribute("startedAt")).isInstanceOf(Long.class);
    interceptor.afterCompletion(req, res, new Object(), null);
    interceptor.afterCompletion(new MockHttpServletRequest(), res, new Object(), null);
  }
}
