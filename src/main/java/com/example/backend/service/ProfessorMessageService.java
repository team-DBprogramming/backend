package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorMessageRecipient;
import com.example.backend.dto.professor.ProfessorMessageRequest;
import com.example.backend.dto.professor.ProfessorMessageResponse;
import com.example.backend.mapper.ProfessorMessageMapper;
import com.example.backend.security.AuthenticatedUser;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorMessageService {

  private static final String NOTIFICATION_TITLE = "교수님으로부터 새 메시지가 도착했습니다.";
  private static final String NOTIFICATION_TYPE = "PROFESSOR_MESSAGE";

  private final ProfessorMessageMapper messageMapper;
  private final Clock clock;

  public ProfessorMessageService(ProfessorMessageMapper messageMapper, Clock clock) {
    this.messageMapper = messageMapper;
    this.clock = clock;
  }

  @Transactional
  public ProfessorMessageResponse sendMessage(
      AuthenticatedUser currentUser, ProfessorMessageRequest request) {
    Long professorUserId = currentUser.requireProfessorUserId();
    validateRequest(request);

    String courseId = request.courseId().trim();
    String division = request.division().trim();
    String message = request.message().trim();
    List<String> studentIds = normalizeStudentIds(request.studentIds());

    Long sectionId = messageMapper.findTargetSectionId(professorUserId, courseId, division);
    if (sectionId == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_COURSE_NOT_FOUND);
    }

    List<ProfessorMessageRecipient> recipients =
        messageMapper.findRecipients(sectionId, studentIds);
    if (recipients.size() != studentIds.size()) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_MESSAGE_INVALID_RECIPIENT);
    }

    Instant now = clock.instant();
    for (ProfessorMessageRecipient recipient : recipients) {
      messageMapper.insertProfessorMessageNotification(
          recipient.userId(),
          professorUserId,
          sectionId,
          NOTIFICATION_TITLE,
          message,
          NOTIFICATION_TYPE,
          now);
    }
    return new ProfessorMessageResponse(recipients.size());
  }

  private void validateRequest(ProfessorMessageRequest request) {
    if (request == null
        || isBlank(request.courseId())
        || isBlank(request.division())
        || isBlank(request.message())
        || request.studentIds() == null
        || request.studentIds().isEmpty()) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_MESSAGE_INVALID_REQUEST);
    }
  }

  private List<String> normalizeStudentIds(List<String> studentIds) {
    List<String> normalized =
        studentIds.stream()
            .filter(studentId -> !isBlank(studentId))
            .map(String::trim)
            .collect(
                java.util.stream.Collectors.collectingAndThen(
                    java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                    List::copyOf));
    if (normalized.isEmpty()) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_MESSAGE_INVALID_REQUEST);
    }
    return normalized;
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
