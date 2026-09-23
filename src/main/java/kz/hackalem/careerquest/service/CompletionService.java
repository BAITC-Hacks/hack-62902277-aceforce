package kz.hackalem.careerquest.service;

import java.util.UUID;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.exception.*;
import kz.hackalem.careerquest.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompletionService {
  private final EmployeeDao employees;
  private final ActivityDao history;
  private final CareerService careers;
  private final RecommendationService recommendations;

  public CompletionService(
      EmployeeDao employees,
      ActivityDao history,
      CareerService careers,
      RecommendationService recommendations) {
    this.employees = employees;
    this.history = history;
    this.careers = careers;
    this.recommendations = recommendations;
  }

  @Transactional
  public void complete(String id, String eventId, String requestId) {
    if (requestId == null || !requestId.matches("[a-zA-Z0-9-]{8,80}"))
      throw new BusinessException("Invalid request id");
    employees.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));
    employees.lock(id);
    String scoped = id + ":" + requestId;
    if (history.requestExists(scoped)) return;
    var p = careers.progress(id);
    if (recommendations.recommend(p, 1000).stream().noneMatch(r -> r.event().id().equals(eventId)))
      throw new BusinessException(
          "Activity is completed, ineligible or does not close a skill gap");
    var started =
        history.findByEmployee(id).stream()
            .filter(a -> a.eventId().equals(eventId) && a.status().equals("in_progress"))
            .findFirst();
    if (started.isPresent())
      history.finishStarted(started.get().id(), recommendations.asOf(), scoped);
    else
      history.insert(
          new ActivityRecord(
              "NEW-" + UUID.randomUUID(),
              id,
              eventId,
              recommendations.asOf(),
              null,
              "completed",
              100,
              null,
              null,
              "self"),
          scoped);
  }
}
