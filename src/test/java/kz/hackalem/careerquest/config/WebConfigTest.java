package kz.hackalem.careerquest.config;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import kz.hackalem.careerquest.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

class WebConfigTest extends IntegrationTestBase {
  @Autowired MessageSource messages;

  @Test
  void allLanguagesResolve() {
    assertThat(messages.getMessage("nav.dashboard", null, Locale.forLanguageTag("kk")))
        .isEqualTo("Менің мансабым");
    assertThat(messages.getMessage("nav.dashboard", null, Locale.ENGLISH)).isEqualTo("My career");
    assertThat(messages.getMessage("nav.dashboard", null, Locale.forLanguageTag("ru")))
        .isEqualTo("Моя карьера");
  }
}
