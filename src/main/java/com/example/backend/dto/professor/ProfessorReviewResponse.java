package com.example.backend.dto.professor;

import java.util.List;

public record ProfessorReviewResponse(
    Summary summary, ItemAverages itemAverages, List<ReviewItem> reviews) {

  public record Summary(Object avgRating, Object participationRate, int participantCount) {}

  public record ItemAverages(Object overall, Object content, Object workload, Object kindness) {}

  public record ReviewItem(
      String reviewId,
      double rating,
      String createdAt,
      String pros,
      String cons,
      String tip,
      String writer) {}
}
