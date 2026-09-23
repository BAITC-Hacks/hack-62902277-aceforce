package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.TestFixtures;
import org.junit.jupiter.api.Test;

class CareerEventTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = TestFixtures.event("A", false);
    assertThat(value.id()).isEqualTo("A");
    assertThat(value.mandatory()).isFalse();
    assertThat(value.prerequisites()).containsEntry("S", 1);
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
