package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.CourseRequestDecisionRequest;
import com.example.backend.dto.professor.CourseRequestDecisionResponse;
import com.example.backend.dto.professor.CourseRequestItem;
import com.example.backend.dto.professor.CourseRequestListResponse;
import com.example.backend.dto.professor.CourseRequestSummary;
import com.example.backend.dto.professor.ProfessorCourseRequestInfo;
import com.example.backend.mapper.ProfessorCourseRequestMapper;
import com.example.backend.security.AuthenticatedUser;
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
  private final Clock clock;

  public ProfessorCourseRequestService(ProfessorCourseRequestMapper requestMapper, Clock clock) {
    this.requestMapper = requestMapper;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public CourseRequestListResponse getRequests(
      AuthenticatedUser currentUser, String courseId, Integer page, Integer size) {
    Long professorUserId = currentUser.requireProfessorUserId();
    int currentPage = positiveOrDefault(page, 1);
    int pageSize = positiveOrDefault(size, 20);
    int offset = (currentPage - 1) * pageSize;

    CourseRequestSummary summary = requestMapper.findRequestSummary(professorUserId, courseId);
    if (summary == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    List<CourseRequestItem> requests =
        requestMapper.findPendingRequests(professorUserId, courseId, pageSize, offset);
    return new CourseRequestListResponse(summary, requests);
  }

  @Transactional
  public CourseRequestDecisionResponse decideRequest(
      AuthenticatedUser currentUser,
      String courseId,
      String requestId,
      CourseRequestDecisionRequest request) {
    Long professorUserId = currentUser.requireProfessorUserId();
    String status = normalizeStatus(request);
    Instant now = clock.instant();

    ProfessorCourseRequestInfo info =
        requestMapper.findRequestForProfessor(professorUserId, courseId, requestId);
    if (info == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    if (!PENDING.equals(info.status())) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_ALREADY_PROCESSED);
    }

    int updated =
        requestMapper.updatePendingRequestStatus(professorUserId, courseId, requestId, status, now);
    if (updated <= 0) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_ALREADY_PROCESSED);
    }

    requestMapper.insertResultNotification(
        info.studentUserId(),
        professorUserId,
        info.sectionId(),
        info.requestPk(),
        notificationTitle(info.courseName(), status),
        notificationBody(info.courseName(), status),
        RESULT_NOTIFICATION_TYPE,
        now);

    return new CourseRequestDecisionResponse(info.requestId(), status, now);
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
