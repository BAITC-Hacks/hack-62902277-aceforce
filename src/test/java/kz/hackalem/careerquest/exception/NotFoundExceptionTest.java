package kz.hackalem.careerquest.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {
  @Test
  void retainsMessage() {
    assertThat(new NotFoundException("test")).hasMessage("test");
  }
}
