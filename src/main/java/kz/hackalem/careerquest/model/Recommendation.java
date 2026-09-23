package kz.hackalem.careerquest.model;

import java.util.List;

public record Recommendation(
    CareerEvent event,
    double score,
    int impact,
    int criticalImpact,
    long previousCompletions,
    long previousDropouts,
    List<String> skillNames,
    boolean continueStarted) {}
