package kz.hackalem.careerquest.model;

import java.util.Map;
import java.util.Set;

public record RoleProfile(
    int id,
    String role,
    String grade,
    Map<String, Integer> requiredSkills,
    Set<String> criticalSkills) {}
