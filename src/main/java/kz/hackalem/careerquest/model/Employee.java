package kz.hackalem.careerquest.model;

import java.time.LocalDate;
import java.util.Map;

public record Employee(
    String id,
    String fullName,
    String department,
    String role,
    String grade,
    String managerId,
    LocalDate hireDate,
    int tenureMonths,
    String workFormat,
    String preferredLanguage,
    LocalDate lastReviewDate,
    Map<String, Integer> skills,
    CareerGoal goal) {}
