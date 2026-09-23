package kz.hackalem.careerquest.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import kz.hackalem.careerquest.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class DashboardControllerTest extends IntegrationTestBase {
  @Autowired MockMvc mvc;

  @Test
  void rendersDashboardAndLanguages() throws Exception {
    for (String lang : new String[] {"en", "ru", "kk"})
      mvc.perform(get("/").param("lang", lang).with(user("hr").roles("HR")))
          .andExpect(status().isOk())
          .andExpect(view().name("dashboard/index"));
  }

  @Test
  void rendersHistoryPaginationAndAchievements() throws Exception {
    mvc.perform(get("/activities").param("page", "999").with(user("employee").roles("EMPLOYEE")))
        .andExpect(status().isOk());
    mvc.perform(get("/achievements").with(user("employee").roles("EMPLOYEE")))
        .andExpect(status().isOk());
  }

  @Test
  void employeeCannotReadAnotherEmployee() throws Exception {
    mvc.perform(get("/").param("employeeId", "E0002").with(user("employee").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());
  }

  @Test
  void goalPostRedirectsAndPersists() throws Exception {
    mvc.perform(
            post("/employees/E0001/goal")
                .with(user("employee").roles("EMPLOYEE"))
                .with(csrf())
                .param("targetRole", "Backend Engineer")
                .param("targetGrade", "Senior"))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void unknownEmployeeGives404() throws Exception {
    mvc.perform(get("/").param("employeeId", "missing").with(user("hr").roles("HR")))
        .andExpect(status().isNotFound());
  }
}
