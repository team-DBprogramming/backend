package com.example.backend.dto.student;

import java.util.List;

public record StudentReviewListResponse(List<Course> courses, Summary summary) {

  public record Course(
      String courseId, String courseName, String professor, String semester, Integer credit, boolean isCompleted) {}

  public record Summary(Integer completed, Integer pending) {}
}
