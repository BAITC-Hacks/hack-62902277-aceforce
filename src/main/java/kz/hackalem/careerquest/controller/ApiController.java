package kz.hackalem.careerquest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import kz.hackalem.careerquest.controller.support.EmployeeAccess;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.model.*;
import kz.hackalem.careerquest.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
  private final EmployeeDao employees;
  private final CatalogDao catalog;
  private final EventDao events;
  private final ActivityDao history;
  private final CareerService careers;
  private final RecommendationService recommendations;
  private final CompletionService completion;
  private final EmployeeAccess access;

  public ApiController(
      EmployeeDao employees,
      CatalogDao catalog,
      EventDao events,
      ActivityDao history,
      CareerService careers,
      RecommendationService recommendations,
      CompletionService completion,
      EmployeeAccess access) {
    this.employees = employees;
    this.catalog = catalog;
    this.events = events;
    this.history = history;
    this.careers = careers;
    this.recommendations = recommendations;
    this.completion = completion;
    this.access = access;
  }

  @GetMapping("/data/summary")
  public Map<String, Integer> summary() {
    return Map.of(
        "employees",
        employees.findAll().size(),
        "skills",
        catalog.skills().size(),
        "roleProfiles",
        catalog.profiles().size(),
        "events",
        events.findAll().size(),
        "activityRecords",
        history.findAll().size());
  }

  @GetMapping("/employees/{id}/career")
  public Map<String, Object> career(@PathVariable String id, Authentication auth) {
    var p = careers.progress(access.resolve(auth, id));
    return Map.of("progress", p, "recommendations", recommendations.recommend(p, 3));
  }

  @PostMapping("/employees/{id}/goal")
  public CareerProgress goal(
      @PathVariable String id, @Valid @RequestBody CareerGoal goal, Authentication auth) {
    careers.changeGoal(access.resolve(auth, id), goal);
    return careers.progress(id);
  }

  public record CompletionRequest(@NotBlank String eventId, @NotBlank String requestId) {}

  @PostMapping("/employees/{id}/complete")
  public CareerProgress complete(
      @PathVariable String id, @Valid @RequestBody CompletionRequest body, Authentication auth) {
    completion.complete(access.resolve(auth, id), body.eventId(), body.requestId());
    return careers.progress(id);
  }
}
