package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.StudentHandler;
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
import com.example.backend.utils.SemesterUtils;
import com.example.backend.utils.SemesterUtils.Semester;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
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
    String studentId = currentUser.requireStudentId();
    Long sectionId = enrollmentMapper.findSectionId(request.courseId(), normalizeDivision(request.division()));
    if (sectionId == null) {
      throw new StudentHandler(ErrorStatus.STUDENT_COURSE_NOT_FOUND);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    params.put("courseId", request.courseId());
    params.put("sectionId", sectionId);
    enrollmentMapper.callInsertEnroll(params);
    return new StudentMutationResponse(
        String.valueOf(sectionId),
        toEnrollStatus(stringValue(params.get("result"))));
  }

  @Transactional
  public StudentMutationResponse cancel(AuthenticatedUser currentUser, String courseId) {
    String studentId = currentUser.requireStudentId();
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    params.put("courseId", courseId);
    params.put("sectionId", null);
    enrollmentMapper.callCancelEnroll(params);
    return new StudentMutationResponse(
        courseId,
        toCancelStatus(stringValue(params.get("result"))));
  }

  @Transactional(readOnly = true)
  public StudentEnrollmentStatusResponse getStatus(AuthenticatedUser currentUser) {
    String studentId = currentUser.requireStudentId();
    StudentEnrollmentStatus status = getEnrollmentStatus();
    StudentCreditSummary summary = nullToEmptySummary(getCreditSummary(studentId));
    List<StudentEnrolledCourse> courses = getEnrolledCourses(studentId);
    return new StudentEnrollmentStatusResponse(
        status == null ? "CLOSED" : status.getStatus(),
        intValue(summary.getCourseCount()),
        intValue(summary.getApplied()),
        Math.max(0, MAX_CREDITS - intValue(summary.getApplied())),
        courses);
  }

  @Transactional(readOnly = true)
  public StudentEnrollmentSummaryResponse getSummary(AuthenticatedUser currentUser) {
    String studentId = currentUser.requireStudentId();
    StudentCreditSummary summary = getCreditSummary(studentId);
    int applied = summary == null || summary.getApplied() == null ? 0 : summary.getApplied();
    int max = summary == null || summary.getMax() == null ? MAX_CREDITS : summary.getMax();
    int courseCount = summary == null || summary.getCourseCount() == null ? 0 : summary.getCourseCount();
    StudentEnrollmentStatus status = getEnrollmentStatus();
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
    String studentId = currentUser.requireStudentId();
    Semester currentSemester = SemesterUtils.current(LocalDate.now(clock));
    return toTimetableResponse(
        SemesterUtils.format(currentSemester),
        getEnrollmentTimetable(studentId));
  }

  private String normalizeDivision(String division) {
    return division == null || division.trim().isEmpty() ? null : division.replace("분반", "").trim();
  }

  private StudentCreditSummary nullToEmptySummary(StudentCreditSummary summary) {
    return summary == null ? new StudentCreditSummary(0, MAX_CREDITS, 0, 0) : summary;
  }

  private StudentEnrollmentStatus getEnrollmentStatus() {
    Map<String, Object> params = new HashMap<>();
    enrollmentMapper.callGetEnrollStatus(params);
    return new StudentEnrollmentStatus(
        stringValue(params.get("status")),
        timestampDate(params.get("deadline")),
        intValue(params.get("daysLeft")));
  }

  private StudentCreditSummary getCreditSummary(String studentId) {
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    enrollmentMapper.callGetCreditSummary(params);
    return new StudentCreditSummary(
        intValue(params.get("applied")),
        intValue(params.get("max")),
        intValue(params.get("courseCount")),
        intValue(params.get("cartCount")));
  }

  private List<StudentEnrolledCourse> getEnrolledCourses(String studentId) {
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    enrollmentMapper.callGetEnrolledCourses(params);
    return listValue(params.get("courses"));
  }

  private List<StudentTimetableItem> getEnrollmentTimetable(String studentId) {
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    enrollmentMapper.callGetEnrollmentTimetable(params);
    return listValue(params.get("rows"));
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> listValue(Object value) {
    return value instanceof List<?> list ? (List<T>) list : List.of();
  }

  private String stringValue(Object value) {
    return value == null ? null : String.valueOf(value);
  }

  private String toEnrollStatus(String result) {
    if ("ENROLL_SUCCESS".equals(result)) {
      return "ENROLLED";
    }
    return result == null ? "UNKNOWN" : result;
  }

  private String toCancelStatus(String result) {
    if ("DELETE_SUCCESS".equals(result)) {
      return "DROPPED";
    }
    return result == null ? "UNKNOWN" : result;
  }

  private String timestampDate(Object value) {
    if (value == null) {
      return null;
    }
    String text = String.valueOf(value);
    return text.length() >= 10 ? text.substring(0, 10) : text;
  }

  private int intValue(Integer value) {
    return value == null ? 0 : value;
  }

  private int intValue(Object value) {
    if (value == null) {
      return 0;
    }
    if (value instanceof Number number) {
      return number.intValue();
    }
    return Integer.parseInt(String.valueOf(value));
  }

  private StudentTimetableResponse toTimetableResponse(String semester, List<StudentTimetableItem> rows) {
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
    return new StudentTimetableResponse(semester, courses, totalCredit, null);
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
