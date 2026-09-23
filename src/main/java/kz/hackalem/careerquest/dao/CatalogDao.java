package kz.hackalem.careerquest.dao;

import java.util.*;
import kz.hackalem.careerquest.model.*;

public interface CatalogDao {
  List<Skill> skills();

  List<RoleProfile> profiles();
}
