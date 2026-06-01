package com.example.backend.dto.student;

import java.util.List;

public record StudentTimetableResponse(
    String semester,
    List<Course> courses,
    Integer totalCredit,
    Statistics statistics) {

  public record Course(
      String courseId,
      String courseName,
      String courseType,
      List<StudentLectureTime> lectureTimes,
      String room,
      String professor) {}

  public record Statistics(
      Integer totalWeeklyHours, String averageDailyHours, List<String> freeDays, Integer courseCount) {}
}
