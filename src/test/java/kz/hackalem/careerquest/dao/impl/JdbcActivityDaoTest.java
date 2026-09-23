package kz.hackalem.careerquest.dao.impl;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import kz.hackalem.careerquest.IntegrationTestBase;
import kz.hackalem.careerquest.model.ActivityRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JdbcActivityDaoTest extends IntegrationTestBase {
  @Autowired JdbcActivityDao dao;

  @Test
  void readsInsertsAndFinishesHistory() {
    assertThat(dao.findAll()).hasSize(2743);
    var date = LocalDate.of(2026, 10, 1);
    dao.insert(
        new ActivityRecord(
            "TEST", "E0001", "EV_005", date, null, "in_progress", 20, null, null, "self"),
        null);
    dao.finishStarted("TEST", date, "request-test");
    assertThat(dao.requestExists("request-test")).isTrue();
    assertThat(dao.findByEmployee("E0001"))
        .anyMatch(r -> r.id().equals("TEST") && r.status().equals("completed"));
  }
}
