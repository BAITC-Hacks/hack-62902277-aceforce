package kz.hackalem.careerquest;

import java.time.LocalDate;
import java.util.*;
import kz.hackalem.careerquest.model.*;

public final class TestFixtures {
  private TestFixtures() {}

  public static Employee employee(CareerGoal goal) {
    return new Employee(
        "E",
        "Test",
        "IT",
        "Backend Engineer",
        "Junior",
        null,
        LocalDate.of(2025, 1, 1),
        12,
        "remote",
        "en",
        LocalDate.of(2026, 1, 1),
        Map.of("S", 1),
        goal);
  }

  public static CareerEvent event(String id, boolean mandatory) {
    return new CareerEvent(
        id,
        "Workshop",
        "Description",
        "course",
        "self_paced",
        2,
        mandatory,
        List.of("Backend Engineer"),
        List.of("Junior"),
        List.of(new DevelopedSkill("S", 1, 3)),
        Map.of("S", 1),
        List.of());
  }

  public static CareerProgress progress() {
    var profile = new RoleProfile(1, "Backend Engineer", "Middle", Map.of("S", 3), Set.of("S"));
    return new CareerProgress(
        employee(new CareerGoal("Backend Engineer", "Middle")),
        profile,
        Map.of("S", 1),
        33.3,
        List.of(new CareerProgress.Gap("S", "System Design", 1, 3, 2, true)),
        false,
        false);
  }

  public static ActivityRecord record(String id, String event, String status, LocalDate date) {
    return new ActivityRecord(
        id,
        "E",
        event,
        date,
        null,
        status,
        status.equals("completed") ? 100 : 20,
        null,
        null,
        "self");
  }
}
