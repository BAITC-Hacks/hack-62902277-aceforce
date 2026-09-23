package kz.hackalem.careerquest.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.exception.*;
import kz.hackalem.careerquest.model.*;
import kz.hackalem.careerquest.util.SkillMath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CareerService {
  private final EmployeeDao employees;
  private final CatalogDao catalog;
  private final ActivityDao history;
  private final EventDao events;

  public CareerService(
      EmployeeDao employees, CatalogDao catalog, ActivityDao history, EventDao events) {
    this.employees = employees;
    this.catalog = catalog;
    this.history = history;
    this.events = events;
  }

  public CareerProgress progress(String id) {
    Employee e =
        employees.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));
    CareerGoal goal =
        e.goal() == null ? new CareerGoal(e.role(), SkillMath.nextGrade(e.grade())) : e.goal();
    RoleProfile target =
        catalog.profiles().stream()
            .filter(p -> p.role().equals(goal.targetRole()) && p.grade().equals(goal.targetGrade()))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Target profile is missing"));
    Map<String, Integer> effective = new LinkedHashMap<>(e.skills());
    Map<String, CareerEvent> byId =
        events.findAll().stream().collect(Collectors.toMap(CareerEvent::id, Function.identity()));
    history.findByEmployee(id).stream()
        .filter(a -> a.status().equals("completed") && a.date().isAfter(e.lastReviewDate()))
        .sorted(Comparator.comparing(ActivityRecord::date).thenComparing(ActivityRecord::id))
        .forEach(
            a -> {
              CareerEvent event = byId.get(a.eventId());
              if (event != null)
                for (DevelopedSkill s : event.developsSkills())
                  effective.compute(
                      s.skillId(),
                      (key, value) ->
                          SkillMath.applyGain(value == null ? 0 : value, s.gain(), s.maxLevel()));
            });
    Map<String, String> names =
        catalog.skills().stream().collect(Collectors.toMap(Skill::id, Skill::name));
    List<CareerProgress.Gap> gaps = new ArrayList<>();
    int have = 0, need = 0;
    boolean criticalReady = true;
    for (var r : target.requiredSkills().entrySet()) {
      int current = effective.getOrDefault(r.getKey(), 0),
          missing = Math.max(0, r.getValue() - current);
      boolean critical = target.criticalSkills().contains(r.getKey());
      have += Math.min(current, r.getValue());
      need += r.getValue();
      if (critical && missing > 0) criticalReady = false;
      gaps.add(
          new CareerProgress.Gap(
              r.getKey(),
              names.getOrDefault(r.getKey(), r.getKey()),
              current,
              r.getValue(),
              missing,
              critical));
    }
    gaps.sort(
        Comparator.comparing(CareerProgress.Gap::critical)
            .reversed()
            .thenComparing(Comparator.comparingInt(CareerProgress.Gap::missing).reversed())
            .thenComparing(CareerProgress.Gap::skillId));
    double percent = need == 0 ? 100 : Math.round(1000.0 * have / need) / 10.0;
    return new CareerProgress(e, target, effective, percent, gaps, criticalReady, e.goal() == null);
  }

  @Transactional
  public void changeGoal(String id, CareerGoal goal) {
    employees.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));
    RoleProfile profile =
        catalog.profiles().stream()
            .filter(p -> p.role().equals(goal.targetRole()) && p.grade().equals(goal.targetGrade()))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Unknown target role or grade"));
    employees.lock(id);
    employees.saveGoal(id, profile.id());
  }
}
