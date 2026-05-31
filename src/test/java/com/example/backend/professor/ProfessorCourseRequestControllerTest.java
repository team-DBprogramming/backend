package com.example.backend.professor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.ProfessorCourseRequestController;
import com.example.backend.dto.professor.CourseRequestItem;
import com.example.backend.dto.professor.CourseRequestDecisionResponse;
import com.example.backend.dto.professor.CourseRequestListResponse;
import com.example.backend.dto.professor.CourseRequestSummary;
import com.example.backend.service.ProfessorCourseRequestService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfessorCourseRequestController.class)
class ProfessorCourseRequestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProfessorCourseRequestService requestService;

  @Test
  void decideRequestReturnsCreatedResponse() throws Exception {
    when(requestService.decideRequest(
            eq("Bearer access-token"), eq("CSE301"), eq("req-db-001"), any()))
        .thenReturn(
            new CourseRequestDecisionResponse(
                "req-db-001", "APPROVED", Instant.parse("2026-05-21T22:15:30Z")));

    mockMvc
        .perform(
            patch("/professors/me/courses/CSE301/requests/req-db-001")
                .header("Authorization", "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "status": "APPROVED"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("PROFESSOR201"))
        .andExpect(jsonPath("$.message").value("수강 요청 처리가 완료되었습니다."))
        .andExpect(jsonPath("$.result.requestId").value("req-db-001"))
        .andExpect(jsonPath("$.result.status").value("APPROVED"))
        .andExpect(jsonPath("$.result.updatedAt").value("2026-05-21T22:15:30Z"));
  }

  @Test
  void getRequestsReturnsProjectApiResponseFormat() throws Exception {
    when(requestService.getRequests(eq("Bearer access-token"), eq("CSE301"), eq(1), eq(20)))
        .thenReturn(
            new CourseRequestListResponse(
                new CourseRequestSummary("데이터베이스개론", "CSE301", "01분반", "2026-1학기", 6, 3),
                List.of(
                    new CourseRequestItem(
                        "req-db-001",
                        "2024111111",
                        "홍길동",
                        3,
                        "컴퓨터공학과",
                        "2026-05-15 14:30",
                        "전공 필수 과목으로 수강이 필요합니다."))));

    mockMvc
        .perform(
            get("/professors/me/courses/CSE301/requests")
                .header("Authorization", "Bearer access-token")
                .queryParam("page", "1")
                .queryParam("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("PROFESSOR200"))
        .andExpect(jsonPath("$.result.summary.courseName").value("데이터베이스개론"))
        .andExpect(jsonPath("$.result.summary.courseId").value("CSE301"))
        .andExpect(jsonPath("$.result.summary.requestCount").value(3))
        .andExpect(jsonPath("$.result.requests[0].requestId").value("req-db-001"))
        .andExpect(jsonPath("$.result.requests[0].studentId").value("2024111111"));
  }
}
