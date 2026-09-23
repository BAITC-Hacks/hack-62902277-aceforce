package kz.hackalem.careerquest.service;

import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import kz.hackalem.careerquest.dao.*;
import kz.hackalem.careerquest.model.*;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatasetImportService {
  private final JdbcTemplate jdbc;
  private final ObjectMapper mapper;
  private final ResourceLoader resources;
  private final EventDao events;
  private final ActivityDao activities;
  private final String location;

  public DatasetImportService(
      JdbcTemplate jdbc,
      ObjectMapper mapper,
      ResourceLoader resources,
      EventDao events,
      ActivityDao activities,
      @Value("${career.dataset-location:classpath:data/}") String location) {
    this.jdbc = jdbc;
    this.mapper = mapper;
    this.resources = resources;
    this.events = events;
    this.activities = activities;
    this.location = location.endsWith("/") ? location : location + "/";
  }

  private JsonNode read(String file) throws IOException {
    try (var in = resources.getResource(location + file).getInputStream()) {
      return mapper.readTree(in);
    }
  }

  private List<String> strings(JsonNode node) {
    List<String> out = new ArrayList<>();
    node.forEach(x -> out.add(x.asText()));
    return out;
  }

  private Map<String, Integer> integers(JsonNode node) {
    Map<String, Integer> out = new LinkedHashMap<>();
    node.fields().forEachRemaining(x -> out.put(x.getKey(), x.getValue().asInt()));
    return out;
  }

  private String nullable(JsonNode node) {
    return node == null || node.isNull() ? null : node.asText();
  }

  private Integer number(String v) {
    return v.isBlank() ? null : Integer.valueOf(v);
  }

  @Transactional(rollbackFor = Exception.class)
  public void importOnce() throws IOException {
    if (jdbc.queryForObject("SELECT COUNT(*) FROM dataset_import", Integer.class) > 0) return;
    JsonNode skillDoc = read("skills.json");
    for (JsonNode s : skillDoc.path("skills")) {
      jdbc.update(
          "INSERT INTO skill VALUES (?,?,?,?,?)",
          s.path("skill_id").asText(),
          s.path("name").asText(),
          s.path("type").asText(),
          s.path("category").asText(),
          s.path("description").asText());
    }
    int profileId = 0;
    Map<String, Integer> profileIds = new HashMap<>();
    for (JsonNode p : skillDoc.path("role_profiles")) {
      int id = ++profileId;
      String role = p.path("role").asText(), grade = p.path("grade").asText();
      profileIds.put(role + "|" + grade, id);
      jdbc.update("INSERT INTO role_profile VALUES (?,?,?)", id, role, grade);
      Set<String> critical = new HashSet<>(strings(p.path("critical_skills")));
      for (var r : integers(p.path("required_skills")).entrySet())
        jdbc.update(
            "INSERT INTO role_requirement VALUES (?,?,?,?)",
            id,
            r.getKey(),
            r.getValue(),
            critical.contains(r.getKey()));
    }
    JsonNode employeeNodes = read("employees.json").path("employees");
    for (JsonNode e : employeeNodes) {
      String id = e.path("employee_id").asText();
      jdbc.update(
          "INSERT INTO employee VALUES (?,?,?,?,?,?,?,?,?,?,?)",
          id,
          e.path("full_name").asText(),
          e.path("department").asText(),
          e.path("role").asText(),
          e.path("grade").asText(),
          null,
          LocalDate.parse(e.path("hire_date").asText()),
          e.path("tenure_months").asInt(),
          e.path("work_format").asText(),
          e.path("preferred_language").asText(),
          LocalDate.parse(e.path("last_review_date").asText()));
      for (var s : integers(e.path("skills")).entrySet())
        jdbc.update("INSERT INTO employee_skill VALUES (?,?,?)", id, s.getKey(), s.getValue());
      JsonNode goal = e.path("career_goal");
      if (!goal.isNull() && !goal.isMissingNode()) {
        Integer pid =
            profileIds.get(
                goal.path("target_role").asText() + "|" + goal.path("target_grade").asText());
        if (pid == null)
          throw new IllegalArgumentException("Unknown target role profile for " + id);
        jdbc.update("INSERT INTO career_goal VALUES (?,?)", id, pid);
      }
    }
    for (JsonNode e : employeeNodes)
      jdbc.update(
          "UPDATE employee SET manager_id=? WHERE id=?",
          nullable(e.get("manager_id")),
          e.path("employee_id").asText());
    for (JsonNode e : read("events.json").path("events")) {
      List<DevelopedSkill> developed = new ArrayList<>();
      for (JsonNode s : e.path("develops_skills"))
        developed.add(
            new DevelopedSkill(
                s.path("skill_id").asText(), s.path("gain").asInt(), s.path("max_level").asInt()));
      events.create(
          new CareerEvent(
              e.path("event_id").asText(),
              e.path("title").asText(),
              e.path("description").asText(),
              e.path("type").asText(),
              e.path("format").asText(),
              e.path("duration_hours").asDouble(),
              e.path("mandatory").asBoolean(),
              strings(e.path("target_roles")),
              strings(e.path("target_grades")),
              developed,
              integers(e.path("prerequisites")),
              strings(e.path("upcoming_sessions")).stream().map(LocalDate::parse).toList()));
    }
    try (var reader =
            new InputStreamReader(
                resources.getResource(location + "activity_history.csv").getInputStream(),
                StandardCharsets.UTF_8);
        var csv =
            CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get().parse(reader)) {
      for (CSVRecord r : csv)
        activities.insert(
            new ActivityRecord(
                r.get("record_id"),
                r.get("employee_id"),
                r.get("event_id"),
                LocalDate.parse(r.get("date")),
                r.get("due_date").isBlank() ? null : LocalDate.parse(r.get("due_date")),
                r.get("status"),
                Integer.parseInt(r.get("completion_pct")),
                number(r.get("score")),
                number(r.get("feedback_rating")),
                r.get("assigned_by")),
            null);
    }
    jdbc.update("INSERT INTO dataset_import VALUES (1,?)", java.sql.Timestamp.from(Instant.now()));
  }
}
