package kz.hackalem.careerquest.controller;

import kz.hackalem.careerquest.service.HrAnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HrController {
  private final HrAnalyticsService analytics;

  public HrController(HrAnalyticsService analytics) {
    this.analytics = analytics;
  }

  @GetMapping("/hr")
  public String index(Model model) {
    model.addAttribute("analytics", analytics.summary());
    model.addAttribute("isHr", true);
    return "hr/index";
  }
}
