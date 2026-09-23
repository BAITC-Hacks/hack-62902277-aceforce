package kz.hackalem.careerquest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;
import kz.hackalem.careerquest.TestFixtures;
import kz.hackalem.careerquest.dao.ActivityDao;
import org.junit.jupiter.api.Test;

class GamificationServiceTest {
  @Test
  void rewardsOnlyCompletedAndUnlocksBadges() {
    var dao = mock(ActivityDao.class);
    var service = new GamificationService(dao);
    when(dao.findByEmployee("E")).thenReturn(List.of());
    assertThat(service.progress("E").level()).isEqualTo(1);
    assertThat(service.progress("E").badges()).isEmpty();
    when(dao.findByEmployee("E"))
        .thenReturn(
            IntStream.range(0, 10)
                .mapToObj(i -> TestFixtures.record("" + i, "A", "completed", LocalDate.now()))
                .toList());
    assertThat(service.progress("E").xp()).isEqualTo(1000);
    assertThat(service.progress("E").badges()).containsExactly("first", "learner", "explorer");
  }
}
