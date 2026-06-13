package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorMessageRequest;
import com.example.backend.dto.professor.ProfessorMessageResponse;
import com.example.backend.mapper.ProfessorMessageMapper;
import com.example.backend.security.AuthenticatedUser;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorMessageService {

  private static final String SUCCESS = "SUCCESS";
  private static final String COURSE_NOT_FOUND = "COURSE_NOT_FOUND";
  private static final String INVALID_RECIPIENT = "INVALID_RECIPIENT";
  private static final String INVALID_REQUEST = "INVALID_REQUEST";

  private final ProfessorMessageMapper messageMapper;

  public ProfessorMessageService(ProfessorMessageMapper messageMapper, Clock clock) {
    this.messageMapper = messageMapper;
  }

  @Transactional
  public ProfessorMessageResponse sendMessage(
      AuthenticatedUser currentUser, ProfessorMessageRequest request) {
    validateRequest(request);

    Map<String, Object> params = new HashMap<>();
    params.put("professorUserId", currentUser.requireProfessorUserId());
    params.put("courseId", request.courseId().trim());
    params.put("division", request.division().trim());
    params.put("studentIds", String.join(",", request.studentIds()));
    params.put("message", request.message().trim());

    messageMapper.sendProfessorMessage(params);
    String result = stringValue(params.get("result"));
    if (!SUCCESS.equals(result)) {
      throw mapProcedureFailure(result);
    }

    return new ProfessorMessageResponse(intValue(params.get("sentCount")));
  }

  private ProfessorHandler mapProcedureFailure(String result) {
    if (COURSE_NOT_FOUND.equals(result)) {
      return new ProfessorHandler(ErrorStatus.PROFESSOR_COURSE_NOT_FOUND);
    }
    if (INVALID_RECIPIENT.equals(result)) {
      return new ProfessorHandler(ErrorStatus.PROFESSOR_MESSAGE_INVALID_RECIPIENT);
    }
    if (INVALID_REQUEST.equals(result)) {
      return new ProfessorHandler(ErrorStatus.PROFESSOR_MESSAGE_INVALID_REQUEST);
    }
    return new ProfessorHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
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

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private String stringValue(Object value) {
    return value == null ? null : value.toString();
  }

  private int intValue(Object value) {
    if (value instanceof Number number) {
      return number.intValue();
    }
    if (value == null) {
      return 0;
    }
    return Integer.parseInt(value.toString());
  }
}
