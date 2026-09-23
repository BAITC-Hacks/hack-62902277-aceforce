package kz.hackalem.careerquest.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SkillMathTest {
  @Test
  void appliesCapsWithoutReducingExistingSkill() {
    assertThat(SkillMath.applyGain(4, 1, 3)).isEqualTo(4);
    assertThat(SkillMath.applyGain(2, 2, 3)).isEqualTo(3);
    assertThat(SkillMath.applyGain(5, 9, 9)).isEqualTo(5);
    assertThat(SkillMath.applyGain(2, -2, 4)).isEqualTo(2);
  }

  @Test
  void leadHasNoInventedNextGrade() {
    assertThat(SkillMath.nextGrade("Junior")).isEqualTo("Middle");
    assertThat(SkillMath.nextGrade("Middle")).isEqualTo("Senior");
    assertThat(SkillMath.nextGrade("Senior")).isEqualTo("Lead");
    assertThat(SkillMath.nextGrade("Lead")).isEqualTo("Lead");
    assertThatThrownBy(() -> SkillMath.nextGrade("Unknown"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
