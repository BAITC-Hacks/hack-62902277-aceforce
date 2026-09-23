package kz.hackalem.careerquest.model;

import java.util.List;
import java.util.Map;

public record CareerProgress(
    Employee employee,
    RoleProfile target,
    Map<String, Integer> effectiveSkills,
    double percent,
    List<Gap> gaps,
    boolean criticalReady,
    boolean defaultTarget) {
  public record Gap(
      String skillId, String name, int current, int required, int missing, boolean critical) {}
}
