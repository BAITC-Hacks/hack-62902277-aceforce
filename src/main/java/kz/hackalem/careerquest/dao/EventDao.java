package kz.hackalem.careerquest.dao;

import java.util.*;
import kz.hackalem.careerquest.model.*;

public interface EventDao {
  List<CareerEvent> findAll();

  Optional<CareerEvent> findById(String id);

  void create(CareerEvent event);

  void update(CareerEvent event);

  void delete(String id);
}
