package com.example.backend.dto.professor;

import java.util.List;

public record ProfessorDashboardResponse(
    int courseCount,
    int totalStudents,
    int totalCapacity,
    Object avgSatisfaction,
    int newReviewCount,
    List<TodayScheduleItem> todaySchedule,
    List<AssignedCourseItem> assignedCourses) {

  public record TodayScheduleItem(
      String courseId,
      String courseName,
      int studentCount,
      String startTime,
      String endTime,
      String room,
      String scheduleStatus) {}

  public record AssignedCourseItem(
      String courseId,
      String courseName,
      String division,
      int studentCount,
      int maxStudents,
      Object satisfaction) {}
}
