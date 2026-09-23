package kz.hackalem.careerquest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;
import kz.hackalem.careerquest.*;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.model.*;
import org.junit.jupiter.api.*;

class RecommendationServiceTest {
  EventDao events = mock(EventDao.class);
  ActivityDao history = mock(ActivityDao.class);
  RecommendationService service =
      new RecommendationService(events, history, "2026-10-01", new WeightedScoringPolicy());

  @BeforeEach
  void setup() {
    when(history.findByEmployee("E")).thenReturn(List.of());
  }

  @Test
  void filtersMandatoryAndCompleted() {
    when(events.findAll())
        .thenReturn(
            List.of(
                TestFixtures.event("A", true),
                TestFixtures.event("B", false),
                TestFixtures.event("C", false)));
    when(history.findByEmployee("E"))
        .thenReturn(List.of(TestFixtures.record("1", "B", "completed", LocalDate.of(2026, 2, 1))));
    var result = service.recommend(TestFixtures.progress(), 3);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).event().id()).isEqualTo("C");
    assertThat(result.get(0).criticalImpact()).isEqualTo(1);
    assertThat(result.get(0).previousCompletions()).isEqualTo(1);
  }

  @Test
  void tieBreakUsesEventIdAndLimit() {
    when(events.findAll())
        .thenReturn(List.of(TestFixtures.event("B", false), TestFixtures.event("A", false)));
    assertThat(service.recommend(TestFixtures.progress(), 1).get(0).event().id()).isEqualTo("A");
  }

  @Test
  void unmetPrerequisiteFiltersEvent() {
    var e = TestFixtures.event("A", false);
    when(events.findAll())
        .thenReturn(
            List.of(
                new CareerEvent(
                    e.id(),
                    e.title(),
                    e.description(),
                    e.type(),
                    e.format(),
                    2,
                    false,
                    e.targetRoles(),
                    e.targetGrades(),
                    e.developsSkills(),
                    Map.of("S", 5),
                    List.of())));
    assertThat(service.recommend(TestFixtures.progress(), 3)).isEmpty();
  }

  @Test
  void scheduledEventsNeedFutureSessionUnlessStarted() {
    var e = TestFixtures.event("A", false);
    when(events.findAll())
        .thenReturn(
            List.of(
                new CareerEvent(
                    e.id(),
                    e.title(),
                    e.description(),
                    e.type(),
                    "online",
                    2,
                    false,
                    e.targetRoles(),
                    e.targetGrades(),
                    e.developsSkills(),
                    e.prerequisites(),
                    List.of(LocalDate.of(2020, 1, 1)))));
    assertThat(service.recommend(TestFixtures.progress(), 3)).isEmpty();
    when(history.findByEmployee("E"))
        .thenReturn(
            List.of(TestFixtures.record("1", "A", "in_progress", LocalDate.of(2026, 9, 1))));
    assertThat(service.recommend(TestFixtures.progress(), 3).get(0).continueStarted()).isTrue();
  }

  @Test
  void clubMayRepeatButNotTwiceOnSameDay() {
    when(events.findAll()).thenReturn(List.of(TestFixtures.event("EV_036", false)));
    when(history.findByEmployee("E"))
        .thenReturn(
            List.of(TestFixtures.record("1", "EV_036", "completed", LocalDate.of(2026, 9, 1))));
    assertThat(service.recommend(TestFixtures.progress(), 3)).hasSize(1);
    when(history.findByEmployee("E"))
        .thenReturn(
            List.of(TestFixtures.record("1", "EV_036", "completed", LocalDate.of(2026, 10, 1))));
    assertThat(service.recommend(TestFixtures.progress(), 3)).isEmpty();
  }
}
