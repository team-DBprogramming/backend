package com.example.backend.student;

import static com.example.backend.support.TestAuthentications.studentUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.StudentHandler;
import com.example.backend.dto.student.StudentReviewItem;
import com.example.backend.dto.student.StudentReviewListResponse;
import com.example.backend.dto.student.StudentReviewRequest;
import com.example.backend.mapper.StudentReviewMapper;
import com.example.backend.service.StudentReviewService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentReviewServiceTest {

  private FakeStudentReviewMapper reviewMapper;
  private StudentReviewService reviewService;

  @BeforeEach
  void setUp() {
    reviewMapper = new FakeStudentReviewMapper();
    reviewService = new StudentReviewService(
        reviewMapper, Clock.fixed(Instant.parse("2026-03-01T00:00:00Z"), ZoneOffset.UTC));
  }

  @Test
  void getReviewsIncludesCourseMetadataAndSummary() {
    StudentReviewItem submitted = reviewItem("CSE3033", "데이터베이스 시스템", "한지훈", "2026-1학기", 3, "SUBMITTED");
    StudentReviewItem pending = reviewItem("CSE4050", "소프트웨어공학", "한지훈", "2026-1학기", 3, "PENDING");
    reviewMapper.reviews.add(submitted);
    reviewMapper.reviews.add(pending);

    StudentReviewListResponse response = reviewService.getReviews(studentUser(), "2026-1");

    assertThat(response.courses()).hasSize(2);
    assertThat(response.courses().get(0).courseId()).isEqualTo("CSE3033");
    assertThat(response.courses().get(0).professor()).isEqualTo("한지훈");
    assertThat(response.courses().get(0).semester()).isEqualTo("2026-1학기");
    assertThat(response.courses().get(0).credit()).isEqualTo(3);
    assertThat(response.courses().get(0).isCompleted()).isTrue();
    assertThat(response.courses().get(1).isCompleted()).isFalse();
    assertThat(response.summary().completed()).isEqualTo(1);
    assertThat(response.summary().pending()).isEqualTo(1);
    assertThat(reviewMapper.requestedStudentId).isEqualTo("2024123456");
    assertThat(reviewMapper.requestedYear).isEqualTo(2026);
    assertThat(reviewMapper.requestedSemester).isEqualTo(1);
  }

  @Test
  void submitReviewThrowsWhenEnrollmentDoesNotExist() {
    reviewMapper.insertReviewResult = "ENROLLMENT_NOT_FOUND";

    assertThatThrownBy(() -> reviewService.submitReview(studentUser(), "CSE3033", reviewRequest()))
        .isInstanceOf(StudentHandler.class)
        .hasMessage(ErrorStatus.STUDENT_ENROLLMENT_NOT_FOUND.getMessage());

    assertThat(reviewMapper.insertReviewCalled).isTrue();
  }

  private StudentReviewRequest reviewRequest() {
    return new StudentReviewRequest(5, 3, "좋은 강의였습니다.");
  }

  private StudentReviewItem reviewItem(
      String courseId, String courseName, String professor, String semester, Integer credit, String status) {
    StudentReviewItem item = new StudentReviewItem();
    item.setCourseId(courseId);
    item.setCourseName(courseName);
    item.setProfessor(professor);
    item.setSemester(semester);
    item.setCredit(credit);
    item.setStatus(status);
    return item;
  }

  private static class FakeStudentReviewMapper implements StudentReviewMapper {
    private final List<StudentReviewItem> reviews = new ArrayList<>();
    private String requestedStudentId;
    private Integer requestedYear;
    private Integer requestedSemester;
    private String insertReviewResult = "SUCCESS";
    private boolean insertReviewCalled;

    @Override
    public List<StudentReviewItem> findReviews(String studentId, Integer year, Integer semester) {
      requestedStudentId = studentId;
      requestedYear = year;
      requestedSemester = semester;
      return reviews;
    }

    @Override
    public void callInsertReview(Map<String, Object> params) {
      insertReviewCalled = true;
      params.put("result", insertReviewResult);
      if ("SUCCESS".equals(insertReviewResult)) {
        params.put("reviewId", "1");
        params.put("submittedAt", "2026-06-02 14:30:00");
      }
    }
  }
}
