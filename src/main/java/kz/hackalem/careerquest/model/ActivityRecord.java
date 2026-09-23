package kz.hackalem.careerquest.model;

import java.time.LocalDate;

public record ActivityRecord(
    String id,
    String employeeId,
    String eventId,
    LocalDate date,
    LocalDate dueDate,
    String status,
    int completionPct,
    Integer score,
    Integer feedbackRating,
    String assignedBy) {}
