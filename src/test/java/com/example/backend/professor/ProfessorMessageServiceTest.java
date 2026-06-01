package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorMessageRecipient;
import com.example.backend.dto.professor.ProfessorMessageRequest;
import com.example.backend.dto.professor.ProfessorMessageResponse;
import com.example.backend.mapper.ProfessorMessageMapper;
import com.example.backend.service.ProfessorMessageService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
  void sendMessageCreatesProfessorMessageNotificationsForSelectedStudents() {
    messageMapper.sectionId = 101L;
    messageMapper.recipients.add(new ProfessorMessageRecipient(1L, "2024111111"));
    messageMapper.recipients.add(new ProfessorMessageRecipient(2L, "2024222222"));

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
    assertThat(messageMapper.requestedStudentIds).containsExactly("2024111111", "2024222222");
    assertThat(messageMapper.insertedRecipientUserIds).containsExactly(1L, 2L);
    assertThat(messageMapper.insertedSenderUserId).isEqualTo(10L);
    assertThat(messageMapper.insertedSectionId).isEqualTo(101L);
    assertThat(messageMapper.insertedTitle).isEqualTo("교수님으로부터 새 메시지가 도착했습니다.");
    assertThat(messageMapper.insertedBody).isEqualTo("다음 수업 전까지 과제 제출 여부를 확인해주세요.");
    assertThat(messageMapper.insertedType).isEqualTo("PROFESSOR_MESSAGE");
  }

  @Test
  void sendMessageRejectsStudentsOutsideSelectedCourseSection() {
    messageMapper.sectionId = 101L;
    messageMapper.recipients.add(new ProfessorMessageRecipient(1L, "2024111111"));

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
    private Long sectionId;
    private final List<ProfessorMessageRecipient> recipients = new ArrayList<>();
    private Long requestedProfessorUserId;
    private String requestedCourseId;
    private String requestedDivision;
    private List<String> requestedStudentIds;
    private final List<Long> insertedRecipientUserIds = new ArrayList<>();
    private Long insertedSenderUserId;
    private Long insertedSectionId;
    private String insertedTitle;
    private String insertedBody;
    private String insertedType;

    @Override
    public Long findTargetSectionId(Long professorUserId, String courseId, String division) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      requestedDivision = division;
      return sectionId;
    }

    @Override
    public List<ProfessorMessageRecipient> findRecipients(Long sectionId, List<String> studentIds) {
      requestedStudentIds = studentIds;
      return recipients;
    }

    @Override
    public void insertProfessorMessageNotification(
        Long recipientUserId,
        Long senderUserId,
        Long targetSectionId,
        String title,
        String body,
        String type,
        Instant createdAt) {
      insertedRecipientUserIds.add(recipientUserId);
      insertedSenderUserId = senderUserId;
      insertedSectionId = targetSectionId;
      insertedTitle = title;
      insertedBody = body;
      insertedType = type;
    }
  }
}
