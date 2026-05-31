package com.example.backend.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorAssignedCourse;
import com.example.backend.dto.professor.ProfessorDashboardResponse;
import com.example.backend.dto.professor.ProfessorDashboardSummary;
import com.example.backend.dto.professor.ProfessorTodaySchedule;
import com.example.backend.mapper.ProfessorDashboardMapper;
import com.example.backend.service.ProfessorDashboardService;
import com.example.backend.utils.JwtTokenProvider;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorDashboardServiceTest {

  private final Clock clock =
      Clock.fixed(Instant.parse("2026-05-30T02:00:00Z"), ZoneId.of("Asia/Seoul"));
  private FakeProfessorDashboardMapper dashboardMapper;
  private JwtTokenProvider tokenProvider;
  private ProfessorDashboardService dashboardService;

  @BeforeEach
  void setUp() {
    dashboardMapper = new FakeProfessorDashboardMapper();
    tokenProvider =
        new JwtTokenProvider(
            "test-secret-key-test-secret-key-test-secret-key",
            Duration.ofMinutes(30),
            Duration.ofDays(1),
            Duration.ofDays(30),
            clock);
    dashboardService = new ProfessorDashboardService(dashboardMapper, tokenProvider, clock);
  }

  @Test
  void dashboardReturnsProfessorSummarySchedulesAndAssignedCourses() {
    String accessToken = tokenProvider.createAccessToken(10L, "P1001", "PROFESSOR").token();
    dashboardMapper.summary = new ProfessorDashboardSummary(3, 73, 100, 4.5, 5);
    dashboardMapper.todaySchedules.add(
        new ProfessorTodaySchedule("CSE301", "Database", 28, "10:30", "12:00", "A 301"));
    dashboardMapper.todaySchedules.add(
        new ProfessorTodaySchedule("CSE401", "Database Design", 20, "13:00", "14:30", "B 205"));
    dashboardMapper.assignedCourses.add(
        new ProfessorAssignedCourse("CSE301", "Database", "01분반", 28, 35, 4.5));
    dashboardMapper.assignedCourses.add(
        new ProfessorAssignedCourse("CSE401", "Database Design", "01분반", 20, 30, 4.7));

    ProfessorDashboardResponse response =
        dashboardService.getDashboard("Bearer " + accessToken, "2026-1");

    assertThat(dashboardMapper.requestedUserId).isEqualTo(10L);
    assertThat(dashboardMapper.requestedSemester).isEqualTo("2026-1");
    assertThat(dashboardMapper.requestedDayOfWeek).isEqualTo("SAT");
    assertThat(response.courseCount()).isEqualTo(3);
    assertThat(response.totalStudents()).isEqualTo(73);
    assertThat(response.totalCapacity()).isEqualTo(100);
    assertThat(response.avgSatisfaction()).isEqualTo(4.5);
    assertThat(response.newReviewCount()).isEqualTo(5);
    assertThat(response.todaySchedule()).hasSize(2);
    assertThat(response.todaySchedule().get(0).scheduleStatus()).isEqualTo("IN_PROGRESS");
    assertThat(response.todaySchedule().get(1).scheduleStatus()).isEqualTo("SCHEDULED");
    assertThat(response.assignedCourses()).hasSize(2);
  }

  @Test
  void dashboardUsesDashWhenSatisfactionIsMissing() {
    String accessToken = tokenProvider.createAccessToken(10L, "P1001", "PROFESSOR").token();
    dashboardMapper.summary = new ProfessorDashboardSummary(1, 0, 30, null, 0);
    dashboardMapper.assignedCourses.add(
        new ProfessorAssignedCourse("CSE301", "Database", "01분반", 0, 30, null));

    ProfessorDashboardResponse response = dashboardService.getDashboard("Bearer " + accessToken, null);

    assertThat(response.avgSatisfaction()).isEqualTo("-");
    assertThat(response.assignedCourses().get(0).satisfaction()).isEqualTo("-");
  }

  @Test
  void dashboardRejectsNonProfessorAccessToken() {
    String accessToken = tokenProvider.createAccessToken(10L, "2024123456", "STUDENT").token();

    assertThatThrownBy(() -> dashboardService.getDashboard("Bearer " + accessToken, "2026-1"))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4031"));
  }

  private static class FakeProfessorDashboardMapper implements ProfessorDashboardMapper {
    private Long requestedUserId;
    private String requestedSemester;
    private String requestedDayOfWeek;
    private ProfessorDashboardSummary summary;
    private final List<ProfessorTodaySchedule> todaySchedules = new ArrayList<>();
    private final List<ProfessorAssignedCourse> assignedCourses = new ArrayList<>();

    @Override
    public ProfessorDashboardSummary findDashboardSummary(Long userId, String semester) {
      requestedUserId = userId;
      requestedSemester = semester;
      return summary;
    }

    @Override
    public List<ProfessorTodaySchedule> findTodaySchedules(
        Long userId, String semester, String dayOfWeek) {
      requestedUserId = userId;
      requestedSemester = semester;
      requestedDayOfWeek = dayOfWeek;
      return todaySchedules;
    }

    @Override
    public List<ProfessorAssignedCourse> findAssignedCourses(Long userId, String semester) {
      requestedUserId = userId;
      requestedSemester = semester;
      return assignedCourses;
    }
  }
}
