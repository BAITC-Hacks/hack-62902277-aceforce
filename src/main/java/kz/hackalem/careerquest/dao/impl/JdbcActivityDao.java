package kz.hackalem.careerquest.dao.impl;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import kz.hackalem.careerquest.dao.ActivityDao;
import kz.hackalem.careerquest.model.ActivityRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcActivityDao implements ActivityDao {
  private final JdbcTemplate jdbc;

  public JdbcActivityDao(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private ActivityRecord map(ResultSet r, int n) throws SQLException {
    var due = r.getDate("due_date");
    return new ActivityRecord(
        r.getString("id"),
        r.getString("employee_id"),
        r.getString("event_id"),
        r.getDate("activity_date").toLocalDate(),
        due == null ? null : due.toLocalDate(),
        r.getString("status"),
        r.getInt("completion_pct"),
        r.getObject("score", Integer.class),
        r.getObject("feedback_rating", Integer.class),
        r.getString("assigned_by"));
  }

  public List<ActivityRecord> findAll() {
    return jdbc.query("SELECT * FROM activity_record ORDER BY activity_date,id", this::map);
  }

  public List<ActivityRecord> findByEmployee(String id) {
    return jdbc.query(
        "SELECT * FROM activity_record WHERE employee_id=? ORDER BY activity_date,id",
        this::map,
        id);
  }

  public void insert(ActivityRecord r, String requestId) {
    jdbc.update(
        "INSERT INTO activity_record VALUES (?,?,?,?,?,?,?,?,?,?,?)",
        r.id(),
        r.employeeId(),
        r.eventId(),
        r.date(),
        r.dueDate(),
        r.status(),
        r.completionPct(),
        r.score(),
        r.feedbackRating(),
        r.assignedBy(),
        requestId);
  }

  public boolean requestExists(String id) {
    return jdbc.queryForObject(
            "SELECT COUNT(*) FROM activity_record WHERE request_id=?", Integer.class, id)
        > 0;
  }

  public void finishStarted(String id, LocalDate date, String requestId) {
    jdbc.update(
        "UPDATE activity_record SET"
            + " status='completed',completion_pct=100,activity_date=?,request_id=? WHERE id=? AND"
            + " status='in_progress'",
        date,
        requestId,
        id);
  }
}
