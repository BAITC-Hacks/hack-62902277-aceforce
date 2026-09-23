package kz.hackalem.careerquest.config;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import kz.hackalem.careerquest.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

class SecurityConfigTest extends IntegrationTestBase {
  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate jdbc;
  @Autowired PasswordEncoder encoder;

  @Test
  void hashesPasswordsAndAuthenticates() throws Exception {
    String hash =
        jdbc.queryForObject(
            "SELECT password_hash FROM app_user WHERE username='employee'", String.class);
    assertThat(hash).startsWith("$2");
    assertThat(encoder.matches("CareerDemo123!", hash)).isTrue();
    mvc.perform(formLogin("/login").user("employee").password("CareerDemo123!"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void loginAndAssetsPublicButDashboardProtected() throws Exception {
    mvc.perform(get("/login")).andExpect(status().isOk());
    mvc.perform(get("/css/app.css")).andExpect(status().isOk());
    mvc.perform(get("/")).andExpect(status().is3xxRedirection());
    mvc.perform(formLogin("/login").user("hr").password("bad"))
        .andExpect(redirectedUrl("/login?error"));
  }
}
