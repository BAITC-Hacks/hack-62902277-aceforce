package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.TestFixtures;
import org.junit.jupiter.api.Test;

class CareerProgressTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = TestFixtures.progress();
    assertThat(value.percent()).isEqualTo(33.3);
    assertThat(value.gaps().get(0).critical()).isTrue();
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
