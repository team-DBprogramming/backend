package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.CourseRequestDecisionRequest;
import com.example.backend.dto.professor.CourseRequestDecisionResponse;
import com.example.backend.dto.professor.CourseRequestItem;
import com.example.backend.dto.professor.CourseRequestListResponse;
import com.example.backend.dto.professor.CourseRequestSummary;
import com.example.backend.mapper.ProfessorCourseRequestMapper;
import com.example.backend.security.AuthenticatedUser;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorCourseRequestService {

  private static final String SUCCESS = "SUCCESS";
  private static final String NOT_FOUND = "NOT_FOUND";
  private static final String ALREADY_PROCESSED = "ALREADY_PROCESSED";
  private static final String INVALID_STATUS = "INVALID_STATUS";
  private static final String DIVISION_REQUIRED = "DIVISION_REQUIRED";

  private final ProfessorCourseRequestMapper requestMapper;

  public ProfessorCourseRequestService(ProfessorCourseRequestMapper requestMapper) {
    this.requestMapper = requestMapper;
  }

  @Transactional(readOnly = true)
  public CourseRequestListResponse getRequests(
      AuthenticatedUser currentUser, String courseId, String division, Integer page, Integer size) {
    Long professorUserId = currentUser.requireProfessorUserId();

    CourseRequestSummary summary = requestMapper.findRequestSummary(professorUserId, courseId, division);
    if (summary == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    List<CourseRequestItem> requests =
        requestMapper.findPendingRequests(professorUserId, courseId, division, page, size);
    return new CourseRequestListResponse(summary, requests);
  }

  @Transactional
  public CourseRequestDecisionResponse decideRequest(
      AuthenticatedUser currentUser,
      String courseId,
      String division,
      String requestId,
      CourseRequestDecisionRequest request) {
    Map<String, Object> params = new HashMap<>();
    params.put("professorUserId", currentUser.requireProfessorUserId());
    params.put("courseId", courseId);
    params.put("division", division);
    params.put("requestId", requestId);
    params.put("status", request == null ? null : request.status());

    requestMapper.processCourseRequest(params);
    String result = stringValue(params.get("result"));
    if (!SUCCESS.equals(result)) {
      throw mapProcedureFailure(result);
    }

    return new CourseRequestDecisionResponse(
        stringValue(params.get("outRequestId")),
        stringValue(params.get("outStatus")),
        toInstant(params.get("outUpdatedAt")));
  }

  private ProfessorHandler mapProcedureFailure(String result) {
    if (NOT_FOUND.equals(result)) {
      return new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    if (ALREADY_PROCESSED.equals(result)) {
      return new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_ALREADY_PROCESSED);
    }
    if (INVALID_STATUS.equals(result)) {
      return new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_INVALID_STATUS);
    }
    if (DIVISION_REQUIRED.equals(result)) {
      return new ProfessorHandler(ErrorStatus.PROFESSOR_DIVISION_REQUIRED);
    }
    return new ProfessorHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
  }

  private String stringValue(Object value) {
    return value == null ? null : value.toString();
  }

  private Instant toInstant(Object value) {
    if (value instanceof Instant instant) {
      return instant;
    }
    if (value instanceof Timestamp timestamp) {
      return timestamp.toInstant();
    }
    if (value instanceof LocalDateTime localDateTime) {
      return localDateTime.toInstant(ZoneOffset.UTC);
    }
    throw new ProfessorHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
  }
}
