package com.example.backend.dto.professor;

import java.util.List;

public record ProfessorCourseListResponse(List<CourseItem> courses, Statistics statistics) {

  public record CourseItem(
      String courseId,
      String courseName,
      String division,
      int credit,
      String schedule,
      String room,
      int capacity,
      int enrolled,
      Object avgSatisfaction) {}

  public record Statistics(int totalCourses, int totalStudents, Object avgSatisfaction) {}
}
