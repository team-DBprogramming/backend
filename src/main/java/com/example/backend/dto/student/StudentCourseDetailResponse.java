package com.example.backend.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StudentCourseDetailResponse(
    Overview overview,
    Syllabus syllabus,
    EnrollmentEligibility enrollmentEligibility,
    RequestInfo requestInfo,
    List<StudentCourseReviewItem> reviews) {

  public record Overview(
      String courseName,
      String courseId,
      String professor,
      String courseType,
      Integer credit,
      List<StudentLectureTime> lectureTimes,
      String room,
      Integer capacity,
      Integer enrolled,
      String seatStatus,
      String division,
      String year) {}

  public record Syllabus(
      String textbook,
      GradingCriteria gradingCriteria,
      Integer assignmentCount,
      String professorEmail,
      String officePhone,
      String prerequisite,
      String note) {}

  public record GradingCriteria(
      Integer midterm,
      @JsonProperty("final") Integer finalExam,
      Integer assignment,
      Integer attendance) {}

  public record EnrollmentEligibility(
      boolean enrollable,
      String reasonCode,
      String message,
      boolean isEnrolled,
      boolean canRequest) {}

  public record RequestInfo(
      boolean requestable, String requestReason, String requestStatus, boolean isRegistered) {}
}
