package com.example.backend.service;

import com.example.backend.dto.student.StudentCreditSummary;
import com.example.backend.dto.student.StudentEnrolledCourse;
import com.example.backend.dto.student.StudentEnrollmentRequest;
import com.example.backend.dto.student.StudentEnrollmentStatus;
import com.example.backend.dto.student.StudentEnrollmentStatusResponse;
import com.example.backend.dto.student.StudentEnrollmentSummaryResponse;
import com.example.backend.dto.student.StudentLectureTime;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.dto.student.StudentTimetableItem;
import com.example.backend.dto.student.StudentTimetableResponse;
import com.example.backend.mapper.StudentEnrollmentMapper;
import com.example.backend.security.AuthenticatedUser;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentEnrollmentService {

  private static final int MAX_CREDITS = 18;

  private final StudentEnrollmentMapper enrollmentMapper;
  private final Clock clock;

  public StudentEnrollmentService(StudentEnrollmentMapper enrollmentMapper, Clock clock) {
    this.enrollmentMapper = enrollmentMapper;
    this.clock = clock;
  }

  @Transactional
  public StudentMutationResponse enroll(AuthenticatedUser currentUser, StudentEnrollmentRequest request) {
    Long studentId = enrollmentMapper.findStudentId(currentUser.requireStudentUserId());
    Long sectionId = enrollmentMapper.findSectionId(request.courseId(), normalizeDivision(request.division()));
    enrollmentMapper.insertEnrollment(studentId, sectionId);
    enrollmentMapper.increaseEnrolledCount(sectionId);
    return new StudentMutationResponse(String.valueOf(sectionId), "ENROLLED");
  }

  @Transactional
  public StudentMutationResponse cancel(AuthenticatedUser currentUser, String courseId) {
    Long studentId = enrollmentMapper.findStudentId(currentUser.requireStudentUserId());
    enrollmentMapper.decreaseEnrolledCount(studentId, courseId);
    enrollmentMapper.cancelEnrollment(studentId, courseId);
    return new StudentMutationResponse(courseId, "DROPPED");
  }

  @Transactional(readOnly = true)
  public StudentEnrollmentStatusResponse getStatus(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    StudentEnrollmentStatus status = enrollmentMapper.findEnrollmentStatus(userId);
    StudentCreditSummary summary = nullToEmptySummary(enrollmentMapper.findCreditSummary(userId, MAX_CREDITS));
    List<StudentEnrolledCourse> courses = enrollmentMapper.findEnrolledCourses(userId);
    return new StudentEnrollmentStatusResponse(
        status == null ? "CLOSED" : status.getStatus(),
        intValue(summary.getCourseCount()),
        intValue(summary.getApplied()),
        Math.max(0, MAX_CREDITS - intValue(summary.getApplied())),
        courses);
  }

  @Transactional(readOnly = true)
  public StudentEnrollmentSummaryResponse getSummary(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    StudentCreditSummary summary = enrollmentMapper.findCreditSummary(userId, MAX_CREDITS);
    int applied = summary == null || summary.getApplied() == null ? 0 : summary.getApplied();
    int max = summary == null || summary.getMax() == null ? MAX_CREDITS : summary.getMax();
    int courseCount = summary == null || summary.getCourseCount() == null ? 0 : summary.getCourseCount();
    StudentEnrollmentStatus status = enrollmentMapper.findEnrollmentStatus(userId);
    OffsetDateTime now = OffsetDateTime.now(clock.withZone(ZoneOffset.UTC));
    return new StudentEnrollmentSummaryResponse(
        new StudentEnrollmentSummaryResponse.EnrollmentStatus(
            status == null ? "CLOSED" : status.getStatus(),
            null,
            null,
            status == null ? null : status.getDeadline(),
            status == null ? 0 : status.getDaysLeft(),
            null),
        new StudentEnrollmentSummaryResponse.CreditSummary(applied, courseCount, max, Math.max(0, max - applied)),
        now.toString());
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse getPreview(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    return toTimetableResponse(enrollmentMapper.findEnrollmentTimetable(userId));
  }

  private String normalizeDivision(String division) {
    return division == null || division.trim().isEmpty() ? null : division.replace("분반", "").trim();
  }

  private StudentCreditSummary nullToEmptySummary(StudentCreditSummary summary) {
    return summary == null ? new StudentCreditSummary(0, MAX_CREDITS, 0, 0) : summary;
  }

  private int intValue(Integer value) {
    return value == null ? 0 : value;
  }

  private StudentTimetableResponse toTimetableResponse(List<StudentTimetableItem> rows) {
    Map<String, CourseBuilder> grouped = new LinkedHashMap<>();
    for (StudentTimetableItem row : rows) {
      grouped
          .computeIfAbsent(
              row.getCourseId(),
              key ->
                  new CourseBuilder(
                      row.getCourseId(),
                      row.getCourseName(),
                      row.getCourseType(),
                      row.getRoom(),
                      row.getProfessor(),
                      row.getCredit()))
          .lectureTimes()
          .add(new StudentLectureTime(row.getDayOfWeek(), row.getStartTime(), row.getEndTime()));
    }
    List<StudentTimetableResponse.Course> courses =
        grouped.values().stream()
            .map(
                course ->
                    new StudentTimetableResponse.Course(
                        course.courseId(),
                        course.courseName(),
                        course.courseType(),
                        course.lectureTimes(),
                        course.room(),
                        course.professor()))
            .toList();
    int totalCredit =
        grouped.values().stream()
            .map(CourseBuilder::credit)
            .filter(credit -> credit != null)
            .mapToInt(Integer::intValue)
            .sum();
    return new StudentTimetableResponse(null, courses, totalCredit, null);
  }

  private record CourseBuilder(
      String courseId,
      String courseName,
      String courseType,
      String room,
      String professor,
      Integer credit,
      List<StudentLectureTime> lectureTimes) {

    private CourseBuilder(
        String courseId,
        String courseName,
        String courseType,
        String room,
        String professor,
        Integer credit) {
      this(courseId, courseName, courseType, room, professor, credit, new ArrayList<>());
    }
  }
}
