package kz.hackalem.careerquest.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import kz.hackalem.careerquest.IntegrationTestBase;
import kz.hackalem.careerquest.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class ApiControllerTest extends IntegrationTestBase {
  @Autowired MockMvc mvc;
  @Autowired CareerService careers;
  @Autowired RecommendationService recommendations;
  @Autowired GamificationService game;

  @Test
  void summaryHasRealCounts() throws Exception {
    mvc.perform(get("/api/data/summary").with(user("hr").roles("HR")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.employees").value(200))
        .andExpect(jsonPath("$.skills").value(60))
        .andExpect(jsonPath("$.activityRecords").value(2743));
  }

  @Test
  void profileAndValidatedGoalWork() throws Exception {
    mvc.perform(get("/api/employees/E0001/career").with(user("employee").roles("EMPLOYEE")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.progress.employee.id").value("E0001"));
    mvc.perform(
            post("/api/employees/E0001/goal")
                .with(user("employee").roles("EMPLOYEE"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"targetRole\":\"\",\"targetGrade\":\"Senior\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void completionIncreasesProgressOnceAndSurvivesReload() throws Exception {
    var before = careers.progress("E0001");
    var rec = recommendations.recommend(before, 3).get(0);
    int xp = game.progress("E0001").xp();
    String body = "{\"eventId\":\"" + rec.event().id() + "\",\"requestId\":\"demo-request-12345\"}";
    for (int i = 0; i < 2; i++)
      mvc.perform(
              post("/api/employees/E0001/complete")
                  .with(user("hr").roles("HR"))
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(body))
          .andExpect(status().isOk());
    assertThat(careers.progress("E0001").percent()).isGreaterThan(before.percent());
    assertThat(game.progress("E0001").xp()).isEqualTo(xp + 100);
  }

  @Test
  void csrfBlocksUnprotectedMutation() throws Exception {
    mvc.perform(
            post("/api/employees/E0001/complete")
                .with(user("hr").roles("HR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isForbidden());
  }
}
