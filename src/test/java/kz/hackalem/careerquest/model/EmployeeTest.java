package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.TestFixtures;
import org.junit.jupiter.api.Test;

class EmployeeTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = TestFixtures.employee(null);
    assertThat(value.id()).isEqualTo("E");
    assertThat(value.skills()).containsEntry("S", 1);
    assertThat(value.goal()).isNull();
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
