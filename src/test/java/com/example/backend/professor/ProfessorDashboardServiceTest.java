package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static com.example.backend.support.TestAuthentications.studentUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorAssignedCourse;
import com.example.backend.dto.professor.ProfessorDashboardResponse;
import com.example.backend.dto.professor.ProfessorDashboardResponse.TodayScheduleItem;
import com.example.backend.mapper.ProfessorDashboardMapper;
import com.example.backend.service.ProfessorDashboardService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorDashboardServiceTest {

  private final Clock clock =
      Clock.fixed(Instant.parse("2026-05-30T02:00:00Z"), ZoneId.of("Asia/Seoul"));
  private FakeProfessorDashboardMapper dashboardMapper;
  private ProfessorDashboardService dashboardService;

  @BeforeEach
  void setUp() {
    dashboardMapper = new FakeProfessorDashboardMapper();
    dashboardService = new ProfessorDashboardService(dashboardMapper, clock);
  }

  @Test
  void dashboardReturnsProfessorSummarySchedulesAndAssignedCoursesFromCallableResults() {
    dashboardMapper.courseCount = 3;
    dashboardMapper.totalStudents = 73;
    dashboardMapper.totalCapacity = 100;
    dashboardMapper.avgSatisfaction = 4.5;
    dashboardMapper.newReviewCount = 5;
    dashboardMapper.todaySchedules.add(
        new TodayScheduleItem("CSE301", "Database", "01", 28, "10:30", "12:00", "A 301", "IN_PROGRESS"));
    dashboardMapper.todaySchedules.add(
        new TodayScheduleItem("CSE401", "Database Design", "01", 20, "13:00", "14:30", "B 205", "SCHEDULED"));
    dashboardMapper.assignedCourses.add(
        new ProfessorAssignedCourse("CSE301", "Database", "01", 28, 35, 4.5));
    dashboardMapper.assignedCourses.add(
        new ProfessorAssignedCourse("CSE401", "Database Design", "01", 20, 30, 4.7));

    ProfessorDashboardResponse response = dashboardService.getDashboard(professorUser(), "2026-1");

    assertThat(dashboardMapper.requestedUserId).isEqualTo(10L);
    assertThat(dashboardMapper.requestedSemester).isEqualTo("2026-1");
    assertThat(dashboardMapper.requestedNow).isEqualTo(clock.instant());
    assertThat(response.courseCount()).isEqualTo(3);
    assertThat(response.totalStudents()).isEqualTo(73);
    assertThat(response.totalCapacity()).isEqualTo(100);
    assertThat(response.avgSatisfaction()).isEqualTo(4.5);
    assertThat(response.newReviewCount()).isEqualTo(5);
    assertThat(response.todaySchedule()).hasSize(2);
    assertThat(response.todaySchedule().get(0).division()).isEqualTo("01");
    assertThat(response.todaySchedule().get(0).scheduleStatus()).isEqualTo("IN_PROGRESS");
    assertThat(response.todaySchedule().get(1).scheduleStatus()).isEqualTo("SCHEDULED");
    assertThat(response.assignedCourses()).hasSize(2);
  }

  @Test
  void dashboardUsesDashWhenSatisfactionIsMissing() {
    dashboardMapper.courseCount = 1;
    dashboardMapper.totalCapacity = 30;
    dashboardMapper.assignedCourses.add(
        new ProfessorAssignedCourse("CSE301", "Database", "01", 0, 30, null));

    ProfessorDashboardResponse response = dashboardService.getDashboard(professorUser(), null);

    assertThat(response.avgSatisfaction()).isEqualTo("-");
    assertThat(response.assignedCourses().get(0).satisfaction()).isEqualTo("-");
  }

  @Test
  void dashboardRejectsNonProfessorAccessToken() {
    assertThatThrownBy(() -> dashboardService.getDashboard(studentUser(), "2026-1"))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4031"));
  }

  private static class FakeProfessorDashboardMapper implements ProfessorDashboardMapper {
    private Long requestedUserId;
    private String requestedSemester;
    private Instant requestedNow;
    private Integer courseCount;
    private Integer totalStudents;
    private Integer totalCapacity;
    private Double avgSatisfaction;
    private Integer newReviewCount;
    private final List<TodayScheduleItem> todaySchedules = new ArrayList<>();
    private final List<ProfessorAssignedCourse> assignedCourses = new ArrayList<>();

    @Override
    public void callGetDashboardSummary(Map<String, Object> params) {
      requestedUserId = (Long) params.get("userId");
      requestedSemester = (String) params.get("semester");
      params.put("courseCount", courseCount);
      params.put("totalStudents", totalStudents);
      params.put("totalCapacity", totalCapacity);
      params.put("avgSatisfaction", avgSatisfaction);
      params.put("newReviewCount", newReviewCount);
    }

    @Override
    public void callGetTodaySchedules(Map<String, Object> params) {
      requestedUserId = (Long) params.get("userId");
      requestedSemester = (String) params.get("semester");
      requestedNow = (Instant) params.get("now");
      params.put("rows", todaySchedules);
    }

    @Override
    public void callGetAssignedCourses(Map<String, Object> params) {
      requestedUserId = (Long) params.get("userId");
      requestedSemester = (String) params.get("semester");
      params.put("rows", assignedCourses);
    }
  }
}
