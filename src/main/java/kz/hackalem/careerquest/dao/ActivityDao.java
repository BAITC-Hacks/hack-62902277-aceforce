package kz.hackalem.careerquest.dao;

import java.util.*;
import kz.hackalem.careerquest.model.*;

public interface ActivityDao {
  List<ActivityRecord> findByEmployee(String employeeId);

  List<ActivityRecord> findAll();

  void insert(ActivityRecord record, String requestId);

  boolean requestExists(String requestId);

  void finishStarted(String recordId, java.time.LocalDate date, String requestId);
}
