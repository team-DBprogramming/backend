package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.CourseRequestDecisionRequest;
import com.example.backend.dto.professor.CourseRequestDecisionResponse;
import com.example.backend.dto.professor.CourseRequestItem;
import com.example.backend.dto.professor.CourseRequestListResponse;
import com.example.backend.dto.professor.CourseRequestSummary;
import com.example.backend.mapper.ProfessorCourseRequestMapper;
import com.example.backend.service.ProfessorCourseRequestService;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorCourseRequestServiceTest {

  private FakeProfessorCourseRequestMapper requestMapper;
  private ProfessorCourseRequestService requestService;

  @BeforeEach
  void setUp() {
    requestMapper = new FakeProfessorCourseRequestMapper();
    requestService = new ProfessorCourseRequestService(requestMapper);
  }

  @Test
  void decideRequestUsesCallableProcedureResult() {
    requestMapper.result = "SUCCESS";
    requestMapper.outRequestId = "301";
    requestMapper.outStatus = "APPROVED";
    requestMapper.outUpdatedAt = Timestamp.from(Instant.parse("2026-05-21T22:15:30Z"));

    CourseRequestDecisionResponse response =
        requestService.decideRequest(
            professorUser(),
            "CSE301",
            " 01 ",
            "301",
            new CourseRequestDecisionRequest(" approved "));

    assertThat(response.requestId()).isEqualTo("301");
    assertThat(response.status()).isEqualTo("APPROVED");
    assertThat(response.updatedAt()).isEqualTo(Instant.parse("2026-05-21T22:15:30Z"));
    assertThat(requestMapper.requestedDivision).isEqualTo(" 01 ");
    assertThat(requestMapper.requestedStatus).isEqualTo(" approved ");
  }

  @Test
  void decideRequestMapsAlreadyProcessedProcedureResult() {
    requestMapper.result = "ALREADY_PROCESSED";

    assertThatThrownBy(
            () ->
                requestService.decideRequest(
                    professorUser(),
                    "CSE301",
                    "01",
                    "301",
                    new CourseRequestDecisionRequest("REJECTED")))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4001"));
  }

  @Test
  void decideRequestMapsInvalidStatusProcedureResult() {
    requestMapper.result = "INVALID_STATUS";

    assertThatThrownBy(
            () ->
                requestService.decideRequest(
                    professorUser(),
                    "CSE301",
                    "01",
                    "301",
                    new CourseRequestDecisionRequest("PENDING")))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4002"));
  }

  @Test
  void listRequestsLeavesDivisionAndPagingDefaultsToSql() {
    requestMapper.summary =
        new CourseRequestSummary("Database", "CSE301", "01분반", "2026-1학기", 6, 3);
    requestMapper.requests.add(
        new CourseRequestItem(
            "301",
            "2024111111",
            "Student",
            3,
            "Computer Science",
            "2026-05-15 14:30",
            "Required major course."));

    CourseRequestListResponse response =
        requestService.getRequests(professorUser(), "CSE301", " 01 ", null, null);

    assertThat(response.summary().courseName()).isEqualTo("Database");
    assertThat(response.summary().requestCount()).isEqualTo(3);
    assertThat(response.requests()).hasSize(1);
    assertThat(response.requests().get(0).studentId()).isEqualTo("2024111111");
    assertThat(requestMapper.requestedDivision).isEqualTo(" 01 ");
    assertThat(requestMapper.requestedPage).isNull();
    assertThat(requestMapper.requestedSize).isNull();
  }

  private static class FakeProfessorCourseRequestMapper implements ProfessorCourseRequestMapper {
    private CourseRequestSummary summary;
    private final List<CourseRequestItem> requests = new ArrayList<>();
    private String result;
    private String outRequestId;
    private String outStatus;
    private Timestamp outUpdatedAt;
    private String requestedDivision;
    private String requestedStatus;
    private Integer requestedPage;
    private Integer requestedSize;

    @Override
    public CourseRequestSummary findRequestSummary(
        Long professorUserId, String courseId, String division) {
      requestedDivision = division;
      return summary;
    }

    @Override
    public List<CourseRequestItem> findPendingRequests(
        Long professorUserId, String courseId, String division, Integer page, Integer size) {
      requestedDivision = division;
      requestedPage = page;
      requestedSize = size;
      return requests;
    }

    @Override
    public void processCourseRequest(Map<String, Object> params) {
      requestedDivision = (String) params.get("division");
      requestedStatus = (String) params.get("status");
      params.put("result", result);
      params.put("outRequestId", outRequestId);
      params.put("outStatus", outStatus);
      params.put("outUpdatedAt", outUpdatedAt);
    }
  }
}
