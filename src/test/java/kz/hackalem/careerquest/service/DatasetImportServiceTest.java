package kz.hackalem.careerquest.service;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class DatasetImportServiceTest extends IntegrationTestBase {
  @Autowired DatasetImportService service;
  @Autowired JdbcTemplate jdbc;

  @Test
  void importsRealDatasetAndIsIdempotent() throws Exception {
    service.importOnce();
    service.importOnce();
    assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM employee", Integer.class)).isEqualTo(200);
    assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM skill", Integer.class)).isEqualTo(60);
    assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM role_profile", Integer.class))
        .isEqualTo(32);
    assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM career_event", Integer.class))
        .isEqualTo(40);
    assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM activity_record", Integer.class))
        .isEqualTo(2743);
  }
}
