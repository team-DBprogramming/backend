package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.dto.professor.ProfessorAssignedCourse;
import com.example.backend.dto.professor.ProfessorDashboardResponse;
import com.example.backend.dto.professor.ProfessorDashboardResponse.AssignedCourseItem;
import com.example.backend.dto.professor.ProfessorDashboardResponse.TodayScheduleItem;
import com.example.backend.dto.professor.ProfessorDashboardSummary;
import com.example.backend.dto.professor.ProfessorTodaySchedule;
import com.example.backend.mapper.ProfessorDashboardMapper;
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
public class ProfessorDashboardService {

  private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

  private final ProfessorDashboardMapper dashboardMapper;
  private final Clock clock;

  public ProfessorDashboardService(ProfessorDashboardMapper dashboardMapper, Clock clock) {
    this.dashboardMapper = dashboardMapper;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public ProfessorDashboardResponse getDashboard(AuthenticatedUser currentUser, String semester) {
    Long professorUserId = currentUser.requireProfessorUserId();
    String normalizedSemester = normalizeSemester(semester);
    ZonedDateTime now = ZonedDateTime.now(clock.withZone(SEOUL_ZONE));
    String today = toSchemaDayOfWeek(now.getDayOfWeek());

    ProfessorDashboardSummary summary =
        nullToEmptySummary(dashboardMapper.findDashboardSummary(professorUserId, normalizedSemester));
    List<TodayScheduleItem> todaySchedule =
        dashboardMapper.findTodaySchedules(professorUserId, normalizedSemester, today).stream()
            .map(schedule -> toTodayScheduleItem(schedule, now.toLocalTime()))
            .toList();
    List<AssignedCourseItem> assignedCourses =
        dashboardMapper.findAssignedCourses(professorUserId, normalizedSemester).stream()
            .map(this::toAssignedCourseItem)
            .toList();

    return new ProfessorDashboardResponse(
        intValue(summary.getCourseCount()),
        intValue(summary.getTotalStudents()),
        intValue(summary.getTotalCapacity()),
        satisfactionValue(summary.getAvgSatisfaction()),
        intValue(summary.getNewReviewCount()),
        todaySchedule,
        assignedCourses);
  }

  private TodayScheduleItem toTodayScheduleItem(ProfessorTodaySchedule schedule, LocalTime now) {
    return new TodayScheduleItem(
        schedule.getCourseId(),
        schedule.getCourseName(),
        schedule.getDivision(),
        intValue(schedule.getStudentCount()),
        schedule.getStartTime(),
        schedule.getEndTime(),
        schedule.getRoom(),
        scheduleStatus(schedule.getStartTime(), schedule.getEndTime(), now));
  }

  private AssignedCourseItem toAssignedCourseItem(ProfessorAssignedCourse course) {
    return new AssignedCourseItem(
        course.getCourseId(),
        course.getCourseName(),
        course.getDivision(),
        intValue(course.getStudentCount()),
        intValue(course.getMaxStudents()),
        satisfactionValue(course.getSatisfaction()));
  }

  private String scheduleStatus(String startTime, String endTime, LocalTime now) {
    if (isBlank(startTime) || isBlank(endTime)) {
      return "SCHEDULED";
    }
    LocalTime start = LocalTime.parse(startTime);
    LocalTime end = LocalTime.parse(endTime);
    if (!now.isBefore(start) && now.isBefore(end)) {
      return "IN_PROGRESS";
    }
    if (!now.isBefore(end)) {
      return "COMPLETED";
    }
    return "SCHEDULED";
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

  private ProfessorDashboardSummary nullToEmptySummary(ProfessorDashboardSummary summary) {
    return summary == null ? new ProfessorDashboardSummary(0, 0, 0, null, 0) : summary;
  }

  private Object satisfactionValue(Double value) {
    return value == null ? "-" : value;
  }

  private int intValue(Integer value) {
    return value == null ? 0 : value;
  }

  private String normalizeSemester(String semester) {
    return isBlank(semester) ? null : semester.trim();
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
