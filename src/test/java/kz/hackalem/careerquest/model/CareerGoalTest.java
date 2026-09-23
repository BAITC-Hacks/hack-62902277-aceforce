package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CareerGoalTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = new CareerGoal("Backend Engineer", "Middle");
    assertThat(value.targetRole()).isEqualTo("Backend Engineer");
    assertThat(value.targetGrade()).isEqualTo("Middle");
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
