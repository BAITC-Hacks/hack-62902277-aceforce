package kz.hackalem.careerquest.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import kz.hackalem.careerquest.service.DatasetImportService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

class DataInitializerTest {
  @Test
  void demoUsersRequireStrongPassword() throws Exception {
    var importer = mock(DatasetImportService.class);
    var runner =
        new DataInitializer(
            importer, mock(JdbcTemplate.class), mock(PasswordEncoder.class), true, "short");
    assertThatThrownBy(() -> runner.run(null)).isInstanceOf(IllegalArgumentException.class);
    verify(importer).importOnce();
  }

  @Test
  void disabledDemoDoesNotCreateCredentials() throws Exception {
    var importer = mock(DatasetImportService.class);
    var jdbc = mock(JdbcTemplate.class);
    new DataInitializer(importer, jdbc, mock(PasswordEncoder.class), false, "").run(null);
    verify(importer).importOnce();
    verifyNoInteractions(jdbc);
  }
}
