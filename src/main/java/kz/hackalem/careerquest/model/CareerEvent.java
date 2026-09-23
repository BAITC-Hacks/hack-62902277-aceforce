package kz.hackalem.careerquest.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record CareerEvent(
    String id,
    String title,
    String description,
    String type,
    String format,
    double durationHours,
    boolean mandatory,
    List<String> targetRoles,
    List<String> targetGrades,
    List<DevelopedSkill> developsSkills,
    Map<String, Integer> prerequisites,
    List<LocalDate> upcomingSessions) {}
