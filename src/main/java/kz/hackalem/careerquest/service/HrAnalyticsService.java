package kz.hackalem.careerquest.service;

import java.util.*;
import java.util.stream.Collectors;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.model.*;
import org.springframework.stereotype.Service;

@Service
public class HrAnalyticsService {
  private final EmployeeDao employees;
  private final ActivityDao history;
  private final CareerService careers;
  private final RecommendationService recommendations;

  public HrAnalyticsService(
      EmployeeDao employees,
      ActivityDao history,
      CareerService careers,
      RecommendationService recommendations) {
    this.employees = employees;
    this.history = history;
    this.careers = careers;
    this.recommendations = recommendations;
  }

  public Map<String, Object> summary() {
    var all = employees.findAll();
    Map<String, Long> gaps = new HashMap<>();
    double total = 0;
    int noNext = 0;
    for (var e : all) {
      var p = careers.progress(e.id());
      total += p.percent();
      for (var g : p.gaps()) if (g.missing() > 0) gaps.merge(g.name(), 1L, Long::sum);
      if (recommendations.recommend(p, 1).isEmpty()) noNext++;
    }
    var top =
        gaps.entrySet().stream()
            .sorted(
                Map.Entry.<String, Long>comparingByValue()
                    .reversed()
                    .thenComparing(Map.Entry.comparingByKey()))
            .limit(10)
            .toList();
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("employees", all.size());
    result.put("averageProgress", all.isEmpty() ? 0 : Math.round(total / all.size() * 10) / 10.0);
    result.put("withoutGoal", all.stream().filter(e -> e.goal() == null).count());
    result.put("withoutNextStep", noNext);
    result.put("topGaps", top);
    result.put(
        "statuses",
        history.findAll().stream()
            .collect(
                Collectors.groupingBy(
                    ActivityRecord::status, TreeMap::new, Collectors.counting())));
    return result;
  }
}
