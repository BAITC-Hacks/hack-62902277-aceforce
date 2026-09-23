package kz.hackalem.careerquest.dao.impl;

import java.util.*;
import kz.hackalem.careerquest.dao.CatalogDao;
import kz.hackalem.careerquest.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCatalogDao implements CatalogDao {
  private final JdbcTemplate jdbc;

  public JdbcCatalogDao(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<Skill> skills() {
    return jdbc.query(
        "SELECT * FROM skill ORDER BY id",
        (r, n) ->
            new Skill(
                r.getString("id"),
                r.getString("name"),
                r.getString("type"),
                r.getString("category"),
                r.getString("description")));
  }

  public List<RoleProfile> profiles() {
    return jdbc.query(
        "SELECT * FROM role_profile ORDER BY id",
        (r, n) -> {
          int id = r.getInt("id");
          Map<String, Integer> req = new LinkedHashMap<>();
          Set<String> critical = new HashSet<>();
          jdbc.query(
              "SELECT * FROM role_requirement WHERE profile_id=? ORDER BY skill_id",
              x -> {
                req.put(x.getString("skill_id"), x.getInt("required_level"));
                if (x.getBoolean("critical")) critical.add(x.getString("skill_id"));
              },
              id);
          return new RoleProfile(id, r.getString("role_name"), r.getString("grade"), req, critical);
        });
  }
}
