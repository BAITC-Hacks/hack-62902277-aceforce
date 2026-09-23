package kz.hackalem.careerquest.config;

import java.util.Locale;
import kz.hackalem.careerquest.interceptor.RequestTimingInterceptor;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private final RequestTimingInterceptor timing;

  public WebConfig(RequestTimingInterceptor timing) {
    this.timing = timing;
  }

  @Bean
  LocaleResolver localeResolver() {
    var resolver = new SessionLocaleResolver();
    resolver.setDefaultLocale(Locale.forLanguageTag("ru"));
    return resolver;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    var change = new LocaleChangeInterceptor();
    change.setParamName("lang");
    change.setIgnoreInvalidLocale(true);
    registry.addInterceptor(change);
    registry.addInterceptor(timing);
  }
}
