package kz.hackalem.careerquest.dao.impl;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JdbcCatalogDaoTest extends IntegrationTestBase {
  @Autowired JdbcCatalogDao dao;

  @Test
  void loadsRequirementsAndCriticalSkills() {
    assertThat(dao.skills()).hasSize(60);
    assertThat(dao.profiles()).hasSize(32);
    assertThat(dao.profiles().get(0).requiredSkills()).isNotEmpty();
  }
}
