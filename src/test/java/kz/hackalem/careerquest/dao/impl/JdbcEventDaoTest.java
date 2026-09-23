package kz.hackalem.careerquest.dao.impl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import kz.hackalem.careerquest.IntegrationTestBase;
import kz.hackalem.careerquest.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JdbcEventDaoTest extends IntegrationTestBase {
  @Autowired JdbcEventDao dao;

  @Test
  void fullCrudPreservesChildCollections() {
    var e = dao.findById("EV_005").orElseThrow();
    var copy =
        new CareerEvent(
            "TEST_EVENT",
            e.title(),
            e.description(),
            e.type(),
            e.format(),
            e.durationHours(),
            false,
            e.targetRoles(),
            e.targetGrades(),
            e.developsSkills(),
            e.prerequisites(),
            e.upcomingSessions());
    dao.create(copy);
    assertThat(dao.findById(copy.id()).orElseThrow()).isEqualTo(copy);
    var changed =
        new CareerEvent(
            copy.id(),
            "Changed",
            e.description(),
            e.type(),
            e.format(),
            1,
            false,
            e.targetRoles(),
            e.targetGrades(),
            List.of(),
            e.prerequisites(),
            List.of());
    dao.update(changed);
    assertThat(dao.findById(copy.id()).orElseThrow().title()).isEqualTo("Changed");
    dao.delete(copy.id());
    assertThat(dao.findById(copy.id())).isEmpty();
  }

  @Test
  void injectionCannotDeleteCatalog() {
    dao.delete("' OR 1=1 --");
    assertThat(dao.findAll()).hasSize(40);
  }
}
