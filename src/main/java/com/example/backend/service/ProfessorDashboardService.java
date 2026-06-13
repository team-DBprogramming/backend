package com.example.backend.service;

import com.example.backend.dto.professor.ProfessorAssignedCourse;
import com.example.backend.dto.professor.ProfessorDashboardResponse;
import com.example.backend.dto.professor.ProfessorDashboardResponse.AssignedCourseItem;
import com.example.backend.dto.professor.ProfessorDashboardResponse.TodayScheduleItem;
import com.example.backend.dto.professor.ProfessorDashboardSummary;
import com.example.backend.mapper.ProfessorDashboardMapper;
import com.example.backend.security.AuthenticatedUser;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorDashboardService {

  private final ProfessorDashboardMapper dashboardMapper;
  private final Clock clock;

  public ProfessorDashboardService(ProfessorDashboardMapper dashboardMapper, Clock clock) {
    this.dashboardMapper = dashboardMapper;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public ProfessorDashboardResponse getDashboard(AuthenticatedUser currentUser, String semester) {
    Long professorUserId = currentUser.requireProfessorUserId();
    Instant now = clock.instant();

    ProfessorDashboardSummary summary = getDashboardSummary(professorUserId, semester);
    List<TodayScheduleItem> todaySchedule =
        getTodaySchedules(professorUserId, semester, now);
    List<AssignedCourseItem> assignedCourses =
        getAssignedCourses(professorUserId, semester).stream()
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

  private ProfessorDashboardSummary getDashboardSummary(Long professorUserId, String semester) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", professorUserId);
    params.put("semester", semester);
    dashboardMapper.callGetDashboardSummary(params);
    return new ProfessorDashboardSummary(
        intObject(params.get("courseCount")),
        intObject(params.get("totalStudents")),
        intObject(params.get("totalCapacity")),
        doubleObject(params.get("avgSatisfaction")),
        intObject(params.get("newReviewCount")));
  }

  private List<TodayScheduleItem> getTodaySchedules(
      Long professorUserId, String semester, Instant now) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", professorUserId);
    params.put("semester", semester);
    params.put("now", now);
    dashboardMapper.callGetTodaySchedules(params);
    return listValue(params.get("rows"));
  }

  private List<ProfessorAssignedCourse> getAssignedCourses(Long professorUserId, String semester) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", professorUserId);
    params.put("semester", semester);
    dashboardMapper.callGetAssignedCourses(params);
    return listValue(params.get("rows"));
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

  private Object satisfactionValue(Double value) {
    return value == null ? "-" : value;
  }

  private int intValue(Integer value) {
    return value == null ? 0 : value;
  }

  private Integer intObject(Object value) {
    if (value == null) {
      return 0;
    }
    if (value instanceof Number number) {
      return number.intValue();
    }
    return Integer.valueOf(String.valueOf(value));
  }

  private Double doubleObject(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Number number) {
      return number.doubleValue();
    }
    return Double.valueOf(String.valueOf(value));
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> listValue(Object value) {
    return value instanceof List<?> list ? (List<T>) list : List.of();
  }
}
