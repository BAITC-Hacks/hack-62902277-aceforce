package kz.hackalem.careerquest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import kz.hackalem.careerquest.*;
import kz.hackalem.careerquest.dao.*;
import org.junit.jupiter.api.Test;

class HrAnalyticsServiceTest {
  @Test
  void aggregatesAndHandlesEmptyDataset() {
    var employees = mock(EmployeeDao.class);
    var history = mock(ActivityDao.class);
    var careers = mock(CareerService.class);
    var rec = mock(RecommendationService.class);
    var service = new HrAnalyticsService(employees, history, careers, rec);
    when(employees.findAll()).thenReturn(List.of());
    when(history.findAll()).thenReturn(List.of());
    assertThat(service.summary()).containsEntry("employees", 0);
    when(employees.findAll()).thenReturn(List.of(TestFixtures.employee(null)));
    when(careers.progress("E")).thenReturn(TestFixtures.progress());
    when(rec.recommend(any(), eq(1))).thenReturn(List.of());
    assertThat(service.summary())
        .containsEntry("withoutNextStep", 1)
        .containsEntry("withoutGoal", 1L)
        .containsEntry("averageProgress", 33.3);
  }
}
