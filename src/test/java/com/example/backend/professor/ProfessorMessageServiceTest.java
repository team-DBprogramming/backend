package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorMessageRequest;
import com.example.backend.dto.professor.ProfessorMessageResponse;
import com.example.backend.mapper.ProfessorMessageMapper;
import com.example.backend.service.ProfessorMessageService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorMessageServiceTest {

  private final Clock clock = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC);
  private FakeProfessorMessageMapper messageMapper;
  private ProfessorMessageService messageService;

  @BeforeEach
  void setUp() {
    messageMapper = new FakeProfessorMessageMapper();
    messageService = new ProfessorMessageService(messageMapper, clock);
  }

  @Test
  void sendMessageCallsProfessorMessageProcedure() {
    messageMapper.result = "SUCCESS";
    messageMapper.sentCount = 2;

    ProfessorMessageResponse response =
        messageService.sendMessage(
            professorUser(),
            new ProfessorMessageRequest(
                "CSE301",
                "01",
                List.of("2024111111", "2024222222", "2024111111"),
                "다음 수업 전까지 과제 제출 여부를 확인해주세요."));

    assertThat(response.sentCount()).isEqualTo(2);
    assertThat(messageMapper.requestedProfessorUserId).isEqualTo(10L);
    assertThat(messageMapper.requestedCourseId).isEqualTo("CSE301");
    assertThat(messageMapper.requestedDivision).isEqualTo("01");
    assertThat(messageMapper.requestedStudentIds).isEqualTo("2024111111,2024222222,2024111111");
    assertThat(messageMapper.requestedMessage).isEqualTo("다음 수업 전까지 과제 제출 여부를 확인해주세요.");
  }

  @Test
  void sendMessageMapsInvalidRecipientProcedureResult() {
    messageMapper.result = "INVALID_RECIPIENT";

    assertThatThrownBy(
            () ->
                messageService.sendMessage(
                    professorUser(),
                    new ProfessorMessageRequest(
                        "CSE301", "01", List.of("2024111111", "2024999999"), "확인해주세요.")))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode())
                    .isEqualTo("PROFESSOR4006"));
  }

  @Test
  void sendMessageRejectsBlankRequiredFields() {
    assertThatThrownBy(
            () ->
                messageService.sendMessage(
                    professorUser(), new ProfessorMessageRequest("CSE301", "01", List.of(), " ")))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode())
                    .isEqualTo("PROFESSOR4007"));
  }

  private static class FakeProfessorMessageMapper implements ProfessorMessageMapper {
    private String result = "SUCCESS";
    private Integer sentCount = 0;
    private Long requestedProfessorUserId;
    private String requestedCourseId;
    private String requestedDivision;
    private String requestedStudentIds;
    private String requestedMessage;

    @Override
    public void sendProfessorMessage(Map<String, Object> params) {
      requestedProfessorUserId = (Long) params.get("professorUserId");
      requestedCourseId = (String) params.get("courseId");
      requestedDivision = (String) params.get("division");
      requestedStudentIds = (String) params.get("studentIds");
      requestedMessage = (String) params.get("message");
      params.put("result", result);
      params.put("sentCount", sentCount);
    }
  }
}
