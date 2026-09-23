package kz.hackalem.careerquest.model;

import jakarta.validation.constraints.NotBlank;

public record CareerGoal(@NotBlank String targetRole, @NotBlank String targetGrade) {}
