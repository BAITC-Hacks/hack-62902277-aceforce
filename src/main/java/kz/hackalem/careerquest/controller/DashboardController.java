package kz.hackalem.careerquest.controller;

import jakarta.validation.Valid;
import java.util.*;
import kz.hackalem.careerquest.controller.support.EmployeeAccess;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.model.CareerGoal;
import kz.hackalem.careerquest.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {
  private final EmployeeAccess access;
  private final EmployeeDao employees;
  private final CatalogDao catalog;
  private final ActivityDao activities;
  private final CareerService careers;
  private final RecommendationService recommendations;
  private final CompletionService completion;
  private final GamificationService game;

  public DashboardController(
      EmployeeAccess access,
      EmployeeDao employees,
      CatalogDao catalog,
      ActivityDao activities,
      CareerService careers,
      RecommendationService recommendations,
      CompletionService completion,
      GamificationService game) {
    this.access = access;
    this.employees = employees;
    this.catalog = catalog;
    this.activities = activities;
    this.careers = careers;
    this.recommendations = recommendations;
    this.completion = completion;
    this.game = game;
  }

  @GetMapping("/login")
  public String login() {
    return "auth/login";
  }

  @GetMapping({"/", "/dashboard"})
  public String dashboard(
      @RequestParam(required = false) String employeeId, Authentication auth, Model model) {
    String id = access.resolve(auth, employeeId);
    var p = careers.progress(id);
    model.addAttribute("career", p);
    model.addAttribute("recommendations", recommendations.recommend(p, 3));
    model.addAttribute("game", game.progress(id));
    model.addAttribute("profiles", catalog.profiles());
    model.addAttribute("isHr", access.isHr(auth));
    model.addAttribute(
        "employees", access.isHr(auth) ? employees.findAll() : List.of(p.employee()));
    model.addAttribute(
        "roles", catalog.profiles().stream().map(pf -> pf.role()).distinct().toList());
    model.addAttribute("requestId", UUID.randomUUID().toString());
    return "dashboard/index";
  }

  @PostMapping("/employees/{id}/goal")
  public String goal(
      @PathVariable String id, @Valid @ModelAttribute CareerGoal goal, Authentication auth) {
    careers.changeGoal(access.resolve(auth, id), goal);
    return "redirect:/?employeeId=" + id;
  }

  @PostMapping("/employees/{id}/complete/{eventId}")
  public String complete(
      @PathVariable String id,
      @PathVariable String eventId,
      @RequestParam String requestId,
      Authentication auth) {
    completion.complete(access.resolve(auth, id), eventId, requestId);
    return "redirect:/?employeeId=" + id + "&completed";
  }

  @GetMapping("/activities")
  public String history(
      @RequestParam(required = false) String employeeId,
      @RequestParam(defaultValue = "0") int page,
      Authentication auth,
      Model model) {
    String id = access.resolve(auth, employeeId);
    var all = new ArrayList<>(activities.findByEmployee(id));
    Collections.reverse(all);
    int pages = Math.max(1, (all.size() + 14) / 15),
        current = Math.max(0, Math.min(page, pages - 1)),
        start = current * 15;
    model.addAttribute("records", all.subList(start, Math.min(start + 15, all.size())));
    model.addAttribute("page", current);
    model.addAttribute("pages", pages);
    model.addAttribute("employeeId", id);
    model.addAttribute("isHr", access.isHr(auth));
    return "activities/index";
  }

  @GetMapping("/achievements")
  public String achievements(
      @RequestParam(required = false) String employeeId, Authentication auth, Model model) {
    String id = access.resolve(auth, employeeId);
    model.addAttribute("game", game.progress(id));
    model.addAttribute("employeeId", id);
    model.addAttribute("isHr", access.isHr(auth));
    return "achievements/index";
  }
}
