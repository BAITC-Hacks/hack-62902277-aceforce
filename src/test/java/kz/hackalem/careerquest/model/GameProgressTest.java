package kz.hackalem.careerquest.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GameProgressTest {
  @Test
  void retainsDomainValuesAndRecordEquality() {
    var value = new GameProgress(300, 2, 3, java.util.List.of("first"));
    assertThat(value.xp()).isEqualTo(300);
    assertThat(value.level()).isEqualTo(2);
    assertThat(value).isEqualTo(value);
    assertThat(value.toString()).isNotBlank();
  }
}
