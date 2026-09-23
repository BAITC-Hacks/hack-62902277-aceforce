package kz.hackalem.careerquest.controller.support;

import static org.assertj.core.api.Assertions.*;

import kz.hackalem.careerquest.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

class EmployeeAccessTest extends IntegrationTestBase {
  @Autowired EmployeeAccess access;

  @Test
  void employeeScopeAndHrDefault() {
    var employee =
        new UsernamePasswordAuthenticationToken(
            "employee", "", AuthorityUtils.createAuthorityList("ROLE_EMPLOYEE"));
    var hr =
        new UsernamePasswordAuthenticationToken(
            "hr", "", AuthorityUtils.createAuthorityList("ROLE_HR"));
    assertThat(access.resolve(employee, null)).isEqualTo("E0001");
    assertThat(access.resolve(hr, null)).isEqualTo("E0001");
    assertThat(access.resolve(hr, "E0009")).isEqualTo("E0009");
    assertThatThrownBy(() -> access.resolve(employee, "E0002"))
        .isInstanceOf(AccessDeniedException.class);
  }
}
