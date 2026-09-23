package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.TestFixtures;
import org.junit.jupiter.api.Test;

class ActivityRecordTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = TestFixtures.record("R", "A", "completed", java.time.LocalDate.of(2026, 10, 1));
    assertThat(value.completionPct()).isEqualTo(100);
    assertThat(value.status()).isEqualTo("completed");
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
