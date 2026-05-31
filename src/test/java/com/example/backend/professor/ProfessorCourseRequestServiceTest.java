package com.example.backend.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.CourseRequestDecisionRequest;
import com.example.backend.dto.professor.CourseRequestDecisionResponse;
import com.example.backend.dto.professor.CourseRequestItem;
import com.example.backend.dto.professor.CourseRequestListResponse;
import com.example.backend.dto.professor.CourseRequestSummary;
import com.example.backend.dto.professor.ProfessorCourseRequestInfo;
import com.example.backend.mapper.ProfessorCourseRequestMapper;
import com.example.backend.service.ProfessorCourseRequestService;
import com.example.backend.utils.JwtTokenProvider;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorCourseRequestServiceTest {

  private final Clock clock = Clock.fixed(Instant.parse("2026-05-21T22:15:30Z"), ZoneOffset.UTC);
  private FakeProfessorCourseRequestMapper requestMapper;
  private JwtTokenProvider tokenProvider;
  private ProfessorCourseRequestService requestService;

  @BeforeEach
  void setUp() {
    requestMapper = new FakeProfessorCourseRequestMapper();
    tokenProvider =
        new JwtTokenProvider(
            "test-secret-key-test-secret-key-test-secret-key",
            Duration.ofMinutes(30),
            Duration.ofDays(1),
            Duration.ofDays(30),
            clock);
    requestService = new ProfessorCourseRequestService(requestMapper, tokenProvider, clock);
  }

  @Test
  void decideRequestApprovesPendingRequestAndCreatesNotification() {
    String accessToken = tokenProvider.createAccessToken(10L, "P1001", "PROFESSOR").token();
    requestMapper.info =
        new ProfessorCourseRequestInfo(
            1L, "req-db-001", "PENDING", 20L, 10L, 100L, "데이터베이스개론");

    CourseRequestDecisionResponse response =
        requestService.decideRequest(
            "Bearer " + accessToken,
            "CSE301",
            "req-db-001",
            new CourseRequestDecisionRequest("APPROVED"));

    assertThat(response.requestId()).isEqualTo("req-db-001");
    assertThat(response.status()).isEqualTo("APPROVED");
    assertThat(response.updatedAt()).isEqualTo(Instant.parse("2026-05-21T22:15:30Z"));
    assertThat(requestMapper.updatedStatus).isEqualTo("APPROVED");
    assertThat(requestMapper.notificationRecipientUserId).isEqualTo(20L);
    assertThat(requestMapper.notificationSenderUserId).isEqualTo(10L);
    assertThat(requestMapper.notificationType).isEqualTo("COURSE_REQUEST_RESULT");
  }

  @Test
  void decideRequestRejectsAlreadyProcessedRequest() {
    String accessToken = tokenProvider.createAccessToken(10L, "P1001", "PROFESSOR").token();
    requestMapper.info =
        new ProfessorCourseRequestInfo(
            1L, "req-db-001", "APPROVED", 20L, 10L, 100L, "데이터베이스개론");

    assertThatThrownBy(
            () ->
                requestService.decideRequest(
                    "Bearer " + accessToken,
                    "CSE301",
                    "req-db-001",
                    new CourseRequestDecisionRequest("REJECTED")))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4001"));
  }

  @Test
  void decideRequestRejectsInvalidStatus() {
    String accessToken = tokenProvider.createAccessToken(10L, "P1001", "PROFESSOR").token();

    assertThatThrownBy(
            () ->
                requestService.decideRequest(
                    "Bearer " + accessToken,
                    "CSE301",
                    "req-db-001",
                    new CourseRequestDecisionRequest("PENDING")))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4002"));
  }

  @Test
  void listRequestsReturnsSummaryAndPendingRequestsWithDefaultPaging() {
    String accessToken = tokenProvider.createAccessToken(10L, "P1001", "PROFESSOR").token();
    requestMapper.summary =
        new CourseRequestSummary("데이터베이스개론", "CSE301", "01분반", "2026-1학기", 6, 3);
    requestMapper.requests.add(
        new CourseRequestItem(
            "req-db-001",
            "2024111111",
            "홍길동",
            3,
            "컴퓨터공학과",
            "2026-05-15 14:30",
            "전공 필수 과목으로 수강이 필요합니다."));

    CourseRequestListResponse response =
        requestService.getRequests("Bearer " + accessToken, "CSE301", null, null);

    assertThat(response.summary().courseName()).isEqualTo("데이터베이스개론");
    assertThat(response.summary().requestCount()).isEqualTo(3);
    assertThat(response.requests()).hasSize(1);
    assertThat(response.requests().get(0).studentId()).isEqualTo("2024111111");
    assertThat(requestMapper.requestedSize).isEqualTo(20);
    assertThat(requestMapper.requestedOffset).isZero();
  }

  private static class FakeProfessorCourseRequestMapper implements ProfessorCourseRequestMapper {
    private ProfessorCourseRequestInfo info;
    private CourseRequestSummary summary;
    private final List<CourseRequestItem> requests = new ArrayList<>();
    private String updatedStatus;
    private Long notificationRecipientUserId;
    private Long notificationSenderUserId;
    private String notificationType;
    private int requestedSize;
    private int requestedOffset;

    @Override
    public ProfessorCourseRequestInfo findRequestForProfessor(
        Long professorUserId, String courseId, String requestId) {
      return info;
    }

    @Override
    public CourseRequestSummary findRequestSummary(Long professorUserId, String courseId) {
      return summary;
    }

    @Override
    public List<CourseRequestItem> findPendingRequests(
        Long professorUserId, String courseId, int size, int offset) {
      requestedSize = size;
      requestedOffset = offset;
      return requests;
    }

    @Override
    public int updatePendingRequestStatus(
        Long professorUserId, String courseId, String requestId, String status, Instant processedAt) {
      updatedStatus = status;
      return info != null && "PENDING".equals(info.status()) ? 1 : 0;
    }

    @Override
    public void insertResultNotification(
        Long recipientUserId,
        Long senderUserId,
        Long targetSectionId,
        Long targetRequestId,
        String title,
        String body,
        String type,
        Instant createdAt) {
      notificationRecipientUserId = recipientUserId;
      notificationSenderUserId = senderUserId;
      notificationType = type;
    }
  }
}
