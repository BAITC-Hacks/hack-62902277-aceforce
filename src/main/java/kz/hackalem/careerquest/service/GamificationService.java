package kz.hackalem.careerquest.service;

import java.util.ArrayList;
import kz.hackalem.careerquest.dao.ActivityDao;
import kz.hackalem.careerquest.model.GameProgress;
import org.springframework.stereotype.Service;

@Service
public class GamificationService {
  private final ActivityDao history;

  public GamificationService(ActivityDao history) {
    this.history = history;
  }

  public GameProgress progress(String employeeId) {
    int count =
        (int)
            history.findByEmployee(employeeId).stream()
                .filter(a -> a.status().equals("completed"))
                .count();
    var badges = new ArrayList<String>();
    if (count >= 1) badges.add("first");
    if (count >= 5) badges.add("learner");
    if (count >= 10) badges.add("explorer");
    return new GameProgress(count * 100, 1 + count / 3, count, badges);
  }
}
