package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static com.example.backend.support.TestAuthentications.withProfessorAuthentication;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.ProfessorMessageController;
import com.example.backend.dto.professor.ProfessorMessageRequest;
import com.example.backend.dto.professor.ProfessorMessageResponse;
import com.example.backend.service.ProfessorMessageService;
import com.example.backend.utils.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfessorMessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfessorMessageControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProfessorMessageService messageService;
  @MockitoBean private JwtTokenProvider tokenProvider;

  @Test
  void sendMessageReturnsCreatedProjectApiResponseFormat() throws Exception {
    when(messageService.sendMessage(
            eq(professorUser()),
            eq(
                new ProfessorMessageRequest(
                    "CSE301",
                    "01",
                    java.util.List.of("2024111111", "2024222222"),
                    "다음 수업 전까지 과제 제출 여부를 확인해주세요."))))
        .thenReturn(new ProfessorMessageResponse(2));

    mockMvc
        .perform(
            post("/professors/me/messages")
                .with(withProfessorAuthentication())
                .contentType("application/json")
                .content(
                    """
                    {
                      "courseId": "CSE301",
                      "division": "01",
                      "studentIds": ["2024111111", "2024222222"],
                      "message": "다음 수업 전까지 과제 제출 여부를 확인해주세요."
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("PROFESSOR201"))
        .andExpect(jsonPath("$.message").value("메시지를 전송했습니다."))
        .andExpect(jsonPath("$.result.sentCount").value(2));
  }
}
