package kz.hackalem.careerquest.dao.impl;

import java.sql.*;
import java.util.*;
import kz.hackalem.careerquest.dao.EmployeeDao;
import kz.hackalem.careerquest.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEmployeeDao implements EmployeeDao {
  private final JdbcTemplate jdbc;

  public JdbcEmployeeDao(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private Employee map(ResultSet r, int row) throws SQLException {
    String id = r.getString("id");
    Map<String, Integer> skills = new LinkedHashMap<>();
    jdbc.query(
        "SELECT skill_id,skill_level FROM employee_skill WHERE employee_id=? ORDER BY skill_id",
        x -> {
          skills.put(x.getString(1), x.getInt(2));
        },
        id);
    List<CareerGoal> goals =
        jdbc.query(
            "SELECT p.role_name,p.grade FROM career_goal g JOIN role_profile p ON p.id=g.profile_id"
                + " WHERE g.employee_id=?",
            (x, n) -> new CareerGoal(x.getString(1), x.getString(2)),
            id);
    return new Employee(
        id,
        r.getString("full_name"),
        r.getString("department"),
        r.getString("role_name"),
        r.getString("grade"),
        r.getString("manager_id"),
        r.getDate("hire_date").toLocalDate(),
        r.getInt("tenure_months"),
        r.getString("work_format"),
        r.getString("preferred_language"),
        r.getDate("last_review_date").toLocalDate(),
        skills,
        goals.isEmpty() ? null : goals.get(0));
  }

  public Optional<Employee> findById(String id) {
    return jdbc.query("SELECT * FROM employee WHERE id=?", this::map, id).stream().findFirst();
  }

  public List<Employee> findAll() {
    return jdbc.query("SELECT * FROM employee ORDER BY id", this::map);
  }

  public void saveGoal(String id, int profileId) {
    jdbc.update("DELETE FROM career_goal WHERE employee_id=?", id);
    jdbc.update("INSERT INTO career_goal(employee_id,profile_id) VALUES (?,?)", id, profileId);
  }

  public void lock(String id) {
    jdbc.queryForObject("SELECT id FROM employee WHERE id=? FOR UPDATE", String.class, id);
  }
}
