package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.CourseRequestDecisionRequest;
import com.example.backend.dto.professor.CourseRequestDecisionResponse;
import com.example.backend.dto.professor.CourseRequestItem;
import com.example.backend.dto.professor.CourseRequestListResponse;
import com.example.backend.dto.professor.CourseRequestSummary;
import com.example.backend.dto.professor.ProfessorCourseRequestInfo;
import com.example.backend.mapper.ProfessorCourseRequestMapper;
import com.example.backend.utils.JwtTokenProvider;
import com.example.backend.utils.TokenClaims;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorCourseRequestService {

  private static final String APPROVED = "APPROVED";
  private static final String REJECTED = "REJECTED";
  private static final String PENDING = "PENDING";
  private static final String RESULT_NOTIFICATION_TYPE = "COURSE_REQUEST_RESULT";

  private final ProfessorCourseRequestMapper requestMapper;
  private final JwtTokenProvider tokenProvider;
  private final Clock clock;

  public ProfessorCourseRequestService(
      ProfessorCourseRequestMapper requestMapper, JwtTokenProvider tokenProvider, Clock clock) {
    this.requestMapper = requestMapper;
    this.tokenProvider = tokenProvider;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public CourseRequestListResponse getRequests(
      String authorizationHeader, String courseId, Integer page, Integer size) {
    TokenClaims claims = validateProfessor(authorizationHeader);
    int currentPage = positiveOrDefault(page, 1);
    int pageSize = positiveOrDefault(size, 20);
    int offset = (currentPage - 1) * pageSize;

    CourseRequestSummary summary = requestMapper.findRequestSummary(claims.userId(), courseId);
    if (summary == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    List<CourseRequestItem> requests =
        requestMapper.findPendingRequests(claims.userId(), courseId, pageSize, offset);
    return new CourseRequestListResponse(summary, requests);
  }

  @Transactional
  public CourseRequestDecisionResponse decideRequest(
      String authorizationHeader,
      String courseId,
      String requestId,
      CourseRequestDecisionRequest request) {
    TokenClaims claims = validateProfessor(authorizationHeader);
    String status = normalizeStatus(request);
    Instant now = clock.instant();

    ProfessorCourseRequestInfo info =
        requestMapper.findRequestForProfessor(claims.userId(), courseId, requestId);
    if (info == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    if (!PENDING.equals(info.status())) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_ALREADY_PROCESSED);
    }

    int updated =
        requestMapper.updatePendingRequestStatus(claims.userId(), courseId, requestId, status, now);
    if (updated <= 0) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_ALREADY_PROCESSED);
    }

    requestMapper.insertResultNotification(
        info.studentUserId(),
        claims.userId(),
        info.sectionId(),
        info.requestPk(),
        notificationTitle(info.courseName(), status),
        notificationBody(info.courseName(), status),
        RESULT_NOTIFICATION_TYPE,
        now);

    return new CourseRequestDecisionResponse(info.requestId(), status, now);
  }

  private TokenClaims validateProfessor(String authorizationHeader) {
    if (isBlank(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
    TokenClaims claims =
        tokenProvider.validateAccessToken(authorizationHeader.substring("Bearer ".length()).trim());
    if (!"PROFESSOR".equals(claims.role())) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_FORBIDDEN);
    }
    return claims;
  }

  private String normalizeStatus(CourseRequestDecisionRequest request) {
    if (request == null || isBlank(request.status())) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_INVALID_STATUS);
    }
    String status = request.status().trim().toUpperCase();
    if (!APPROVED.equals(status) && !REJECTED.equals(status)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_INVALID_STATUS);
    }
    return status;
  }

  private String notificationTitle(String courseName, String status) {
    return courseName + " 수강 요청 결과";
  }

  private String notificationBody(String courseName, String status) {
    String result = APPROVED.equals(status) ? "승인" : "거절";
    return courseName + " 수강 요청이 " + result + "되었습니다.";
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private int positiveOrDefault(Integer value, int defaultValue) {
    return value == null || value <= 0 ? defaultValue : value;
  }
}
