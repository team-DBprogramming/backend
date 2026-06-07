package com.example.backend.service;

import com.example.backend.dto.student.StudentCreditSummary;
import com.example.backend.dto.student.StudentDashboardResponse;
import com.example.backend.dto.student.StudentEnrollmentStatus;
import com.example.backend.dto.student.StudentInfo;
import com.example.backend.dto.student.StudentQuickActions;
import com.example.backend.dto.student.StudentTodaySchedule;
import com.example.backend.mapper.StudentDashboardMapper;
import com.example.backend.security.AuthenticatedUser;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentDashboardService {

  private static final int MAX_CREDITS = 18;
  private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

  private final StudentDashboardMapper dashboardMapper;
  private final Clock clock;

  public StudentDashboardService(StudentDashboardMapper dashboardMapper, Clock clock) {
    this.dashboardMapper = dashboardMapper;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public StudentDashboardResponse getDashboard(AuthenticatedUser currentUser, String semester) {
    Long studentUserId = currentUser.requireStudentUserId();
    String normalizedSemester = normalizeSemester(semester);
    ZonedDateTime now = ZonedDateTime.now(clock.withZone(SEOUL_ZONE));
    String today = toSchemaDayOfWeek(now.getDayOfWeek());

    StudentInfo studentInfo = dashboardMapper.findStudentInfo(studentUserId, normalizedSemester);
    StudentEnrollmentStatus enrollmentStatus =
        nullToClosedStatus(dashboardMapper.findEnrollmentStatus(studentUserId, normalizedSemester));
    StudentCreditSummary creditSummary =
        nullToEmptyCreditSummary(dashboardMapper.findCreditSummary(studentUserId, MAX_CREDITS, normalizedSemester));
    List<StudentTodaySchedule> todaySchedule =
        markNextSchedule(dashboardMapper.findTodaySchedules(studentUserId, today, normalizedSemester), now.toLocalTime());
    StudentQuickActions quickActions =
        nullToEmptyQuickActions(dashboardMapper.findQuickActions(studentUserId, normalizedSemester));

    return new StudentDashboardResponse(
        studentInfo, enrollmentStatus, creditSummary, todaySchedule, quickActions);
  }

  private List<StudentTodaySchedule> markNextSchedule(
      List<StudentTodaySchedule> schedules, LocalTime now) {
    StudentTodaySchedule next = null;
    for (StudentTodaySchedule schedule : schedules) {
      if (!isBlank(schedule.getStartTime()) && LocalTime.parse(schedule.getStartTime()).isAfter(now)) {
        next = schedule;
        break;
      }
    }
    for (StudentTodaySchedule schedule : schedules) {
      schedule.setNext(schedule == next);
    }
    return schedules;
  }

  private StudentEnrollmentStatus nullToClosedStatus(StudentEnrollmentStatus status) {
    return status == null ? new StudentEnrollmentStatus("CLOSED", null, 0) : status;
  }

  private StudentCreditSummary nullToEmptyCreditSummary(StudentCreditSummary summary) {
    return summary == null ? new StudentCreditSummary(0, MAX_CREDITS, 0, 0) : summary;
  }

  private StudentQuickActions nullToEmptyQuickActions(StudentQuickActions quickActions) {
    return quickActions == null ? new StudentQuickActions(0, 0) : quickActions;
  }

  private String toSchemaDayOfWeek(DayOfWeek dayOfWeek) {
    return switch (dayOfWeek) {
      case MONDAY -> "MON";
      case TUESDAY -> "TUE";
      case WEDNESDAY -> "WED";
      case THURSDAY -> "THU";
      case FRIDAY -> "FRI";
      case SATURDAY -> "SAT";
      case SUNDAY -> "SUN";
    };
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private String normalize(String value) {
    return isBlank(value) ? null : value.trim();
  }

  private String normalizeSemester(String value) {
    String normalized = normalize(value);
    if (normalized == null) {
      return null;
    }
    normalized = normalized.replaceAll("\\s*-\\s*", "-");
    return normalized.replaceAll("\\s*학기$", "");
  }
}
