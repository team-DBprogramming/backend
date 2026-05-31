package com.example.backend.professor;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.ProfessorDashboardController;
import com.example.backend.dto.professor.ProfessorDashboardResponse;
import com.example.backend.dto.professor.ProfessorDashboardResponse.AssignedCourseItem;
import com.example.backend.dto.professor.ProfessorDashboardResponse.TodayScheduleItem;
import com.example.backend.service.ProfessorDashboardService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfessorDashboardController.class)
class ProfessorDashboardControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProfessorDashboardService dashboardService;

  @Test
  void dashboardReturnsProjectApiResponseFormat() throws Exception {
    when(dashboardService.getDashboard(eq("Bearer access-token"), eq("2026-1")))
        .thenReturn(
            new ProfessorDashboardResponse(
                3,
                73,
                100,
                4.5,
                5,
                List.of(
                    new TodayScheduleItem(
                        "CSE301", "데이터베이스개론", 28, "10:30", "12:00", "A동 301호", "SCHEDULED")),
                List.of(
                    new AssignedCourseItem(
                        "CSE301", "데이터베이스개론", "01분반", 28, 35, 4.5))));

    mockMvc
        .perform(
            get("/professors/me/dashboard")
                .header("Authorization", "Bearer access-token")
                .queryParam("semester", "2026-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("PROFESSOR200"))
        .andExpect(jsonPath("$.result.courseCount").value(3))
        .andExpect(jsonPath("$.result.totalStudents").value(73))
        .andExpect(jsonPath("$.result.totalCapacity").value(100))
        .andExpect(jsonPath("$.result.avgSatisfaction").value(4.5))
        .andExpect(jsonPath("$.result.todaySchedule[0].scheduleStatus").value("SCHEDULED"))
        .andExpect(jsonPath("$.result.assignedCourses[0].division").value("01분반"));
  }
}
