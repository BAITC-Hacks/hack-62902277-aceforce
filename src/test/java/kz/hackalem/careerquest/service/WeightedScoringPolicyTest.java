package kz.hackalem.careerquest.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WeightedScoringPolicyTest {
  @Test
  void capsHistoryAndDurationContributions() {
    var policy = new WeightedScoringPolicy();
    assertThat(policy.score(1, 1, 0, 0, 2, false)).isEqualTo(8.9);
    assertThat(policy.score(1, 1, 100, 100, 100, true)).isEqualTo(8);
  }
}
