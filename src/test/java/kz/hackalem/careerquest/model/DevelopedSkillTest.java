package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DevelopedSkillTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = new DevelopedSkill("S", 1, 3);
    assertThat(value.gain()).isEqualTo(1);
    assertThat(value.maxLevel()).isEqualTo(3);
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
