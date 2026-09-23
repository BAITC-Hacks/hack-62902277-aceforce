package kz.hackalem.careerquest.model;

import java.util.List;

public record GameProgress(int xp, int level, int completed, List<String> badges) {}
