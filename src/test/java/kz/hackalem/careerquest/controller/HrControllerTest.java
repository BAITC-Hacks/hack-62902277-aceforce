package kz.hackalem.careerquest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import kz.hackalem.careerquest.IntegrationTestBase;
import kz.hackalem.careerquest.service.HrAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

class HrControllerTest extends IntegrationTestBase {
  @Autowired MockMvc mvc;
  @MockitoBean HrAnalyticsService analytics;

  @Test
  void hrPageRenders() throws Exception {
    when(analytics.summary())
        .thenReturn(
            Map.of(
                "employees",
                200,
                "averageProgress",
                50.0,
                "withoutGoal",
                12,
                "withoutNextStep",
                3,
                "topGaps",
                List.of(Map.entry("SQL", 20L)),
                "statuses",
                Map.of("completed", 10L)));
    mvc.perform(get("/hr").with(user("hr").roles("HR")))
        .andExpect(status().isOk())
        .andExpect(view().name("hr/index"));
  }

  @Test
  void employeeCannotSeeHr() throws Exception {
    mvc.perform(get("/hr").with(user("employee").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());
  }
}
