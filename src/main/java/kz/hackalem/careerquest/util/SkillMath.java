package kz.hackalem.careerquest.util;

public final class SkillMath {
  private SkillMath() {}

  public static int applyGain(int current, int gain, int cap) {
    return Math.min(5, Math.max(current, Math.min(cap, current + Math.max(0, gain))));
  }

  public static String nextGrade(String grade) {
    return switch (grade) {
      case "Junior" -> "Middle";
      case "Middle" -> "Senior";
      case "Senior", "Lead" -> "Lead";
      default -> throw new IllegalArgumentException("Unknown grade");
    };
  }
}
