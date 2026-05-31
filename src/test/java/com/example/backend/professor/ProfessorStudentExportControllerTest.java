package com.example.backend.professor;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.ProfessorStudentExportController;
import com.example.backend.dto.professor.ProfessorStudentExportFile;
import com.example.backend.service.ProfessorStudentExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfessorStudentExportController.class)
class ProfessorStudentExportControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProfessorStudentExportService exportService;

  @Test
  void exportStudentsReturnsAttachmentResponse() throws Exception {
    when(exportService.exportStudents(
            eq("Bearer access-token"), eq("CSE301"), eq("xlsx"), eq("홍"), eq(3), eq("컴퓨터")))
        .thenReturn(
            new ProfessorStudentExportFile(
                "학생관리_데이터베이스개론_2026-1학기_2026-05-31.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new byte[] {'P', 'K', 1, 2}));

    mockMvc
        .perform(
            get("/professors/me/courses/CSE301/students/export")
                .header("Authorization", "Bearer access-token")
                .queryParam("format", "xlsx")
                .queryParam("keyword", "홍")
                .queryParam("grade", "3")
                .queryParam("major", "컴퓨터"))
        .andExpect(status().isOk())
        .andExpect(
            header()
                .string(
                    "Content-Type",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment;")));
  }
}
