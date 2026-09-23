package kz.hackalem.careerquest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;
import kz.hackalem.careerquest.*;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.exception.BusinessException;
import kz.hackalem.careerquest.model.*;
import org.junit.jupiter.api.*;

class CompletionServiceTest {
  EmployeeDao employees = mock(EmployeeDao.class);
  ActivityDao history = mock(ActivityDao.class);
  CareerService careers = mock(CareerService.class);
  RecommendationService recommendations = mock(RecommendationService.class);
  CompletionService service = new CompletionService(employees, history, careers, recommendations);

  @BeforeEach
  void setup() {
    when(employees.findById("E")).thenReturn(Optional.of(TestFixtures.employee(null)));
    when(careers.progress("E")).thenReturn(TestFixtures.progress());
    when(recommendations.asOf()).thenReturn(LocalDate.of(2026, 10, 1));
    when(recommendations.recommend(any(), eq(1000)))
        .thenReturn(
            List.of(
                new Recommendation(
                    TestFixtures.event("A", false), 10, 1, 1, 0, 0, List.of("S"), false)));
  }

  @Test
  void storesOneCompletion() {
    service.complete("E", "A", "request-123");
    verify(history).insert(any(), eq("E:request-123"));
    verify(employees).lock("E");
  }

  @Test
  void duplicateRequestDoesNotRewardAgain() {
    when(history.requestExists("E:request-123")).thenReturn(true);
    service.complete("E", "A", "request-123");
    verify(history, never()).insert(any(), any());
  }

  @Test
  void rejectsIneligibleEvent() {
    assertThatThrownBy(() -> service.complete("E", "BAD", "request-123"))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void rejectsInvalidRequest() {
    assertThatThrownBy(() -> service.complete("E", "A", "!")).isInstanceOf(BusinessException.class);
  }

  @Test
  void completesExistingEnrollment() {
    when(history.findByEmployee("E"))
        .thenReturn(List.of(TestFixtures.record("R", "A", "in_progress", LocalDate.now())));
    service.complete("E", "A", "request-123");
    verify(history).finishStarted("R", LocalDate.of(2026, 10, 1), "E:request-123");
    verify(history, never()).insert(any(), any());
  }
}
