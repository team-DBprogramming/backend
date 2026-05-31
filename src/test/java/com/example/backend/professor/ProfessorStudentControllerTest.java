package com.example.backend.professor;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.ProfessorStudentController;
import com.example.backend.dto.professor.ProfessorStudentItem;
import com.example.backend.dto.professor.ProfessorStudentListResponse;
import com.example.backend.dto.professor.ProfessorStudentSummary;
import com.example.backend.service.ProfessorStudentService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfessorStudentController.class)
class ProfessorStudentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProfessorStudentService studentService;

  @Test
  void getStudentsReturnsProjectApiResponseFormat() throws Exception {
    when(studentService.getStudents(
            eq("Bearer access-token"),
            eq("CSE301"),
            eq("01"),
            eq("2024"),
            eq(3),
            eq("컴퓨터공학과"),
            eq(1),
            eq(20)))
        .thenReturn(
            new ProfessorStudentListResponse(
                new ProfessorStudentSummary("데이터베이스개론", "CSE301", "01분반", "2026-1학기", 6, 3),
                List.of(
                    new ProfessorStudentItem("2024000001", "정도훈", 3, "컴퓨터공학과", true))));

    mockMvc
        .perform(
            get("/professors/me/courses/CSE301/students")
                .header("Authorization", "Bearer access-token")
                .queryParam("division", "01")
                .queryParam("keyword", "2024")
                .queryParam("grade", "3")
                .queryParam("major", "컴퓨터공학과")
                .queryParam("page", "1")
                .queryParam("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("PROFESSOR200"))
        .andExpect(jsonPath("$.result.summary.courseId").value("CSE301"))
        .andExpect(jsonPath("$.result.summary.division").value("01분반"))
        .andExpect(jsonPath("$.result.students[0].studentId").value("2024000001"))
        .andExpect(jsonPath("$.result.students[0].isRetake").value(true));
  }
}
