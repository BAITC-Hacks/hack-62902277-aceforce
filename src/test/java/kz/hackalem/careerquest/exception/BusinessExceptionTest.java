package kz.hackalem.careerquest.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BusinessExceptionTest {
  @Test
  void retainsMessage() {
    assertThat(new BusinessException("test")).hasMessage("test");
  }
}
