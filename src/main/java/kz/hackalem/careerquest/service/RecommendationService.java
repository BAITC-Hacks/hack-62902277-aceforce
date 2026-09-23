package kz.hackalem.careerquest.service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.model.*;
import kz.hackalem.careerquest.util.SkillMath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {
  private final EventDao events;
  private final ActivityDao history;
  private final LocalDate asOf;
  private final RecommendationScoringPolicy policy;

  public RecommendationService(
      EventDao events,
      ActivityDao history,
      @Value("${career.as-of}") String asOf,
      RecommendationScoringPolicy policy) {
    this.events = events;
    this.history = history;
    this.asOf = LocalDate.parse(asOf);
    this.policy = policy;
  }

  public LocalDate asOf() {
    return asOf;
  }

  public List<Recommendation> recommend(CareerProgress p, int limit) {
    List<CareerEvent> all = events.findAll();
    List<ActivityRecord> records = history.findByEmployee(p.employee().id());
    Map<String, CareerEvent> byId =
        all.stream().collect(Collectors.toMap(CareerEvent::id, Function.identity()));
    Set<String> completed =
        records.stream()
            .filter(a -> a.status().equals("completed"))
            .map(ActivityRecord::eventId)
            .collect(Collectors.toSet());
    List<Recommendation> result = new ArrayList<>();
    for (CareerEvent e : all) {
      boolean repeat = e.id().equals("EV_036");
      boolean doneToday =
          records.stream()
              .anyMatch(
                  a ->
                      a.eventId().equals(e.id())
                          && a.status().equals("completed")
                          && !a.date().isBefore(asOf));
      boolean started =
          records.stream()
              .anyMatch(a -> a.eventId().equals(e.id()) && a.status().equals("in_progress"));
      boolean role =
          e.targetRoles().contains(p.employee().role())
              || e.targetRoles().contains(p.target().role());
      boolean grade =
          e.targetGrades().contains(p.employee().grade())
              || e.targetGrades().contains(p.target().grade());
      if (e.mandatory() || !role || !grade || completed.contains(e.id()) && (!repeat || doneToday))
        continue;
      if (!started
          && !e.format().equals("self_paced")
          && e.upcomingSessions().stream().noneMatch(d -> !d.isBefore(asOf))) continue;
      if (e.prerequisites().entrySet().stream()
          .anyMatch(r -> p.effectiveSkills().getOrDefault(r.getKey(), 0) < r.getValue())) continue;
      int impact = 0, critical = 0;
      List<String> names = new ArrayList<>();
      for (DevelopedSkill s : e.developsSkills()) {
        var gap = p.gaps().stream().filter(g -> g.skillId().equals(s.skillId())).findFirst();
        if (gap.isEmpty()) continue;
        var g = gap.get();
        int gain =
            Math.min(
                g.missing(),
                SkillMath.applyGain(g.current(), s.gain(), s.maxLevel()) - g.current());
        if (gain > 0) {
          impact += gain;
          if (g.critical()) critical += gain;
          names.add(g.name());
        }
      }
      if (impact == 0) continue;
      long successes =
          records.stream()
              .filter(
                  a ->
                      a.status().equals("completed")
                          && byId.containsKey(a.eventId())
                          && byId.get(a.eventId()).format().equals(e.format()))
              .count();
      long drops =
          records.stream()
              .filter(
                  a ->
                      Set.of("dropped", "no_show").contains(a.status())
                          && byId.containsKey(a.eventId())
                          && byId.get(a.eventId()).format().equals(e.format()))
              .count();
      double score = policy.score(impact, critical, successes, drops, e.durationHours(), started);
      result.add(
          new Recommendation(
              e,
              Math.round(score * 100) / 100.0,
              impact,
              critical,
              successes,
              drops,
              names,
              started));
    }
    return result.stream()
        .sorted(
            Comparator.comparingDouble(Recommendation::score)
                .reversed()
                .thenComparing(r -> r.event().id()))
        .limit(Math.max(0, limit))
        .toList();
  }
}
