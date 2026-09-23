package kz.hackalem.careerquest;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class CareerQuestApplicationTest extends IntegrationTestBase {
  @Autowired ApplicationContext context;

  @Test
  void applicationStartsWithAllLayers() {
    assertThat(context.getBean(CareerQuestApplication.class)).isNotNull();
  }
}
