package kz.hackalem.careerquest.dao;

import java.util.*;
import kz.hackalem.careerquest.model.*;

public interface EmployeeDao {
  Optional<Employee> findById(String id);

  List<Employee> findAll();

  void saveGoal(String employeeId, int profileId);

  void lock(String employeeId);
}
