package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SkillTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = new Skill("S", "SQL", "hard", "Data", "Description");
    assertThat(value.id()).isEqualTo("S");
    assertThat(value.name()).isEqualTo("SQL");
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
