package com.example.backend.professor;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.ProfessorCourseController;
import com.example.backend.dto.professor.ProfessorCourseListResponse;
import com.example.backend.dto.professor.ProfessorCourseListResponse.CourseItem;
import com.example.backend.dto.professor.ProfessorCourseListResponse.Statistics;
import com.example.backend.service.ProfessorCourseService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfessorCourseController.class)
class ProfessorCourseControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProfessorCourseService courseService;

  @Test
  void getCoursesReturnsProjectApiResponseFormat() throws Exception {
    when(courseService.getCourses(eq("Bearer access-token"), eq("2026-1"), eq("데이터")))
        .thenReturn(
            new ProfessorCourseListResponse(
                List.of(
                    new CourseItem(
                        "CSE301",
                        "데이터베이스개론",
                        "01분반",
                        3,
                        "월수 10:30-12:00",
                        "A동 301호",
                        35,
                        28,
                        4.5)),
                new Statistics(4, 91, 4.5)));

    mockMvc
        .perform(
            get("/professors/me/courses")
                .header("Authorization", "Bearer access-token")
                .queryParam("semester", "2026-1")
                .queryParam("keyword", "데이터"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("PROFESSOR200"))
        .andExpect(jsonPath("$.result.courses[0].courseId").value("CSE301"))
        .andExpect(jsonPath("$.result.courses[0].courseName").value("데이터베이스개론"))
        .andExpect(jsonPath("$.result.courses[0].avgSatisfaction").value(4.5))
        .andExpect(jsonPath("$.result.courses[0].avgScore").doesNotExist())
        .andExpect(jsonPath("$.result.statistics.totalCourses").value(4));
  }
}
