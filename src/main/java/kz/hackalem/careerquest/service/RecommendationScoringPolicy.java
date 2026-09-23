package kz.hackalem.careerquest.service;

public interface RecommendationScoringPolicy {
  double score(
      int impact,
      int criticalImpact,
      long completed,
      long dropped,
      double duration,
      boolean started);
}
