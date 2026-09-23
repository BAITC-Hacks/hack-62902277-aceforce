package kz.hackalem.careerquest.service;

import org.springframework.stereotype.Component;

@Component
public class WeightedScoringPolicy implements RecommendationScoringPolicy {
  public double score(
      int impact,
      int criticalImpact,
      long completed,
      long dropped,
      double duration,
      boolean started) {
    return impact * 4
        + criticalImpact * 3
        + 2
        + Math.min(2, completed * .25)
        - Math.min(2, dropped * .5)
        - Math.min(2, duration / 20)
        + (started ? 1 : 0);
  }
}
