package kz.hackalem.careerquest.controller.support;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EmployeeAccess {
  private final JdbcTemplate jdbc;

  public EmployeeAccess(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public boolean isHr(Authentication auth) {
    return auth != null
        && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_HR"));
  }

  public String resolve(Authentication auth, String requested) {
    if (isHr(auth)) return requested == null || requested.isBlank() ? "E0001" : requested;
    String own =
        jdbc
            .query(
                "SELECT employee_id FROM app_user WHERE username=?",
                (r, n) -> r.getString(1),
                auth.getName())
            .stream()
            .findFirst()
            .orElseThrow(() -> new AccessDeniedException("No employee mapping"));
    if (requested != null && !requested.equals(own))
      throw new AccessDeniedException("Access to another employee is forbidden");
    return own;
  }
}
