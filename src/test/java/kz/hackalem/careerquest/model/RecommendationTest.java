package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.TestFixtures;
import org.junit.jupiter.api.Test;

class RecommendationTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value =
        new Recommendation(
            TestFixtures.event("A", false), 8.9, 1, 1, 2, 0, java.util.List.of("S"), false);
    assertThat(value.impact()).isEqualTo(1);
    assertThat(value.skillNames()).containsExactly("S");
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
