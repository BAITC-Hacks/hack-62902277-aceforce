package kz.hackalem.careerquest.dao.impl;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JdbcEmployeeDaoTest extends IntegrationTestBase {
  @Autowired JdbcEmployeeDao dao;

  @Test
  void readsProfilesAndPersistsGoal() {
    assertThat(dao.findAll()).hasSize(200);
    assertThat(dao.findById("E0001").orElseThrow().skills()).isNotEmpty();
    dao.lock("E0001");
    dao.saveGoal("E0001", 3);
    assertThat(dao.findById("E0001").orElseThrow().goal().targetGrade()).isEqualTo("Senior");
  }

  @Test
  void sqlInjectionIsJustAParameter() {
    assertThat(dao.findById("' OR 1=1 --")).isEmpty();
  }
}
