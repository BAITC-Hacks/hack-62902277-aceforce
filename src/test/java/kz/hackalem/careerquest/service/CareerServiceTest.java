package kz.hackalem.careerquest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;
import kz.hackalem.careerquest.*;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.exception.*;
import kz.hackalem.careerquest.model.*;
import org.junit.jupiter.api.*;

class CareerServiceTest {
  EmployeeDao employees = mock(EmployeeDao.class);
  CatalogDao catalog = mock(CatalogDao.class);
  ActivityDao history = mock(ActivityDao.class);
  EventDao events = mock(EventDao.class);
  CareerService service = new CareerService(employees, catalog, history, events);

  @BeforeEach
  void setup() {
    when(employees.findById("E")).thenReturn(Optional.of(TestFixtures.employee(null)));
    when(catalog.profiles()).thenReturn(List.of(TestFixtures.progress().target()));
    when(catalog.skills()).thenReturn(List.of(new Skill("S", "System Design", "hard", "Tech", "")));
    when(events.findAll()).thenReturn(List.of(TestFixtures.event("EV", false)));
    when(history.findByEmployee("E")).thenReturn(List.of());
  }

  @Test
  void calculatesDefaultTargetAndCriticalGap() {
    var p = service.progress("E");
    assertThat(p.percent()).isEqualTo(33.3);
    assertThat(p.defaultTarget()).isTrue();
    assertThat(p.criticalReady()).isFalse();
    assertThat(p.gaps().get(0).missing()).isEqualTo(2);
  }

  @Test
  void onlyReplaysCompletedAfterReview() {
    when(history.findByEmployee("E"))
        .thenReturn(
            List.of(
                TestFixtures.record("1", "EV", "completed", LocalDate.of(2025, 12, 31)),
                TestFixtures.record("2", "EV", "completed", LocalDate.of(2026, 1, 1)),
                TestFixtures.record("3", "EV", "completed", LocalDate.of(2026, 2, 1)),
                TestFixtures.record("4", "EV", "dropped", LocalDate.of(2026, 3, 1))));
    assertThat(service.progress("E").effectiveSkills()).containsEntry("S", 2);
  }

  @Test
  void missingEmployeeIs404() {
    assertThatThrownBy(() -> service.progress("unknown")).isInstanceOf(NotFoundException.class);
  }

  @Test
  void rejectsUnknownTarget() {
    assertThatThrownBy(() -> service.changeGoal("E", new CareerGoal("Invalid", "Lead")))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void persistsValidatedTarget() {
    service.changeGoal("E", new CareerGoal("Backend Engineer", "Middle"));
    verify(employees).saveGoal("E", 1);
  }
}
