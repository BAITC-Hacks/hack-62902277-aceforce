package kz.hackalem.careerquest.config;

import kz.hackalem.careerquest.service.DatasetImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
  private final DatasetImportService importer;
  private final JdbcTemplate jdbc;
  private final PasswordEncoder encoder;
  private final boolean demo;
  private final String password;

  public DataInitializer(
      DatasetImportService importer,
      JdbcTemplate jdbc,
      PasswordEncoder encoder,
      @Value("${career.demo-users:false}") boolean demo,
      @Value("${career.demo-password:}") String password) {
    this.importer = importer;
    this.jdbc = jdbc;
    this.encoder = encoder;
    this.demo = demo;
    this.password = password;
  }

  public void run(ApplicationArguments args) throws Exception {
    importer.importOnce();
    if (demo) {
      if (password.length() < 12)
        throw new IllegalArgumentException("Demo password must contain at least 12 characters");
      add("hr", "ROLE_HR", null);
      add("employee", "ROLE_EMPLOYEE", "E0001");
    }
  }

  private void add(String user, String role, String employee) {
    if (jdbc.queryForObject("SELECT COUNT(*) FROM app_user WHERE username=?", Integer.class, user)
        == 0)
      jdbc.update(
          "INSERT INTO app_user VALUES (?,?,?,?)", user, encoder.encode(password), role, employee);
  }
}
