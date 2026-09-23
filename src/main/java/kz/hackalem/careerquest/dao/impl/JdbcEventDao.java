package kz.hackalem.careerquest.dao.impl;

import java.sql.*;
import java.util.*;
import kz.hackalem.careerquest.dao.EventDao;
import kz.hackalem.careerquest.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcEventDao implements EventDao {
  private final JdbcTemplate jdbc;

  public JdbcEventDao(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private CareerEvent map(ResultSet r, int row) throws SQLException {
    String id = r.getString("id");
    List<DevelopedSkill> developed = new ArrayList<>();
    Map<String, Integer> pre = new LinkedHashMap<>();
    jdbc.query(
        "SELECT * FROM event_skill WHERE event_id=? ORDER BY skill_id",
        x -> {
          if (x.getInt("gain") > 0)
            developed.add(
                new DevelopedSkill(
                    x.getString("skill_id"), x.getInt("gain"), x.getInt("max_level")));
          if (x.getInt("prerequisite_level") > 0)
            pre.put(x.getString("skill_id"), x.getInt("prerequisite_level"));
        },
        id);
    return new CareerEvent(
        id,
        r.getString("title"),
        r.getString("description"),
        r.getString("event_type"),
        r.getString("event_format"),
        r.getDouble("duration_hours"),
        r.getBoolean("mandatory"),
        jdbc.queryForList(
            "SELECT role_name FROM event_role WHERE event_id=? ORDER BY role_name",
            String.class,
            id),
        jdbc.queryForList(
            "SELECT grade FROM event_grade WHERE event_id=? ORDER BY grade", String.class, id),
        developed,
        pre,
        jdbc.query(
            "SELECT session_date FROM event_session WHERE event_id=? ORDER BY session_date",
            (x, n) -> x.getDate(1).toLocalDate(),
            id));
  }

  public List<CareerEvent> findAll() {
    return jdbc.query("SELECT * FROM career_event ORDER BY id", this::map);
  }

  public Optional<CareerEvent> findById(String id) {
    return jdbc.query("SELECT * FROM career_event WHERE id=?", this::map, id).stream().findFirst();
  }

  @Transactional
  public void create(CareerEvent e) {
    jdbc.update(
        "INSERT INTO career_event VALUES (?,?,?,?,?,?,?)",
        e.id(),
        e.title(),
        e.description(),
        e.type(),
        e.format(),
        e.durationHours(),
        e.mandatory());
    children(e);
  }

  @Transactional
  public void update(CareerEvent e) {
    jdbc.update(
        "UPDATE career_event SET"
            + " title=?,description=?,event_type=?,event_format=?,duration_hours=?,mandatory=?"
            + " WHERE id=?",
        e.title(),
        e.description(),
        e.type(),
        e.format(),
        e.durationHours(),
        e.mandatory(),
        e.id());
    jdbc.update("DELETE FROM event_role WHERE event_id=?", e.id());
    jdbc.update("DELETE FROM event_grade WHERE event_id=?", e.id());
    jdbc.update("DELETE FROM event_skill WHERE event_id=?", e.id());
    jdbc.update("DELETE FROM event_session WHERE event_id=?", e.id());
    children(e);
  }

  private void children(CareerEvent e) {
    for (String role : e.targetRoles())
      jdbc.update("INSERT INTO event_role VALUES (?,?)", e.id(), role);
    for (String grade : e.targetGrades())
      jdbc.update("INSERT INTO event_grade VALUES (?,?)", e.id(), grade);
    Map<String, DevelopedSkill> gains = new LinkedHashMap<>();
    for (DevelopedSkill s : e.developsSkills()) gains.put(s.skillId(), s);
    Set<String> ids = new LinkedHashSet<>(gains.keySet());
    ids.addAll(e.prerequisites().keySet());
    for (String id : ids) {
      DevelopedSkill s = gains.getOrDefault(id, new DevelopedSkill(id, 0, 5));
      jdbc.update(
          "INSERT INTO event_skill VALUES (?,?,?,?,?)",
          e.id(),
          id,
          s.gain(),
          s.maxLevel(),
          e.prerequisites().getOrDefault(id, 0));
    }
    for (var date : e.upcomingSessions())
      jdbc.update("INSERT INTO event_session VALUES (?,?)", e.id(), date);
  }

  public void delete(String id) {
    jdbc.update("DELETE FROM career_event WHERE id=?", id);
  }
}
