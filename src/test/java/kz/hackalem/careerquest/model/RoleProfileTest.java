package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.TestFixtures;
import org.junit.jupiter.api.Test;

class RoleProfileTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = TestFixtures.progress().target();
    assertThat(value.requiredSkills()).containsEntry("S", 3);
    assertThat(value.criticalSkills()).contains("S");
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
