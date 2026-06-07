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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentReviewServiceTest {

  private FakeStudentReviewMapper reviewMapper;
  private StudentReviewService reviewService;

  @BeforeEach
  void setUp() {
    reviewMapper = new FakeStudentReviewMapper();
    reviewService = new StudentReviewService(reviewMapper);
  }

  @Test
  void getReviewsIncludesCourseMetadataAndSummary() {
    StudentReviewItem submitted = reviewItem("CSE3033", "데이터베이스 시스템", "한지훈", "2026-1", 3, "SUBMITTED");
    StudentReviewItem pending = reviewItem("CSE4050", "소프트웨어공학", "한지훈", "2026-1", 3, "PENDING");
    reviewMapper.reviews.add(submitted);
    reviewMapper.reviews.add(pending);

    StudentReviewListResponse response = reviewService.getReviews(studentUser(), "2026-1");

    assertThat(response.courses()).hasSize(2);
    assertThat(response.currentSemester()).isEqualTo("2026-1");
    assertThat(response.courses().get(0).courseId()).isEqualTo("CSE3033");
    assertThat(response.courses().get(0).professor()).isEqualTo("한지훈");
    assertThat(response.courses().get(0).semester()).isEqualTo("2026-1");
    assertThat(response.courses().get(0).credit()).isEqualTo(3);
    assertThat(response.courses().get(0).isCompleted()).isTrue();
    assertThat(response.courses().get(1).isCompleted()).isFalse();
    assertThat(response.summary().completed()).isEqualTo(1);
    assertThat(response.summary().pending()).isEqualTo(1);
    assertThat(reviewMapper.requestedUserId).isEqualTo(1L);
    assertThat(reviewMapper.requestedSemester).isEqualTo("2026-1");
  }

  @Test
  void getReviewsUsesCurrentSemesterWhenSemesterIsMissing() {
    StudentReviewListResponse response = reviewService.getReviews(studentUser(), null);

    assertThat(response.currentSemester()).isEqualTo("2026-1");
    assertThat(reviewMapper.currentSemesterRequested).isTrue();
    assertThat(reviewMapper.requestedSemester).isNull();
  }

  @Test
  void submitReviewThrowsWhenStudentDoesNotExist() {
    reviewMapper.studentId = null;

    assertThatThrownBy(() -> reviewService.submitReview(studentUser(), "CSE3033", reviewRequest()))
        .isInstanceOf(StudentHandler.class)
        .hasMessage(ErrorStatus.STUDENT_NOT_FOUND.getMessage());

    assertThat(reviewMapper.insertedEnrollmentId).isNull();
  }

  @Test
  void submitReviewThrowsWhenEnrollmentDoesNotExist() {
    reviewMapper.enrollmentId = null;

    assertThatThrownBy(() -> reviewService.submitReview(studentUser(), "CSE3033", reviewRequest()))
        .isInstanceOf(StudentHandler.class)
        .hasMessage(ErrorStatus.STUDENT_ENROLLMENT_NOT_FOUND.getMessage());

    assertThat(reviewMapper.insertedEnrollmentId).isNull();
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
    private Long requestedUserId;
    private String requestedSemester;
    private Long studentId = 1L;
    private Long enrollmentId = 1L;
    private Long insertedEnrollmentId;
    private boolean currentSemesterRequested;

    @Override
    public Long findStudentId(Long userId) {
      return studentId;
    }

    @Override
    public Long findEnrollmentId(Long studentId, String courseId) {
      return enrollmentId;
    }

    @Override
    public String findCurrentSemester() {
      currentSemesterRequested = true;
      return "2026-1";
    }

    @Override
    public List<StudentReviewItem> findReviews(Long userId, String semester) {
      requestedUserId = userId;
      requestedSemester = semester;
      return reviews;
    }

    @Override
    public void insertReview(Long enrollmentId, StudentReviewRequest request) {
      insertedEnrollmentId = enrollmentId;
    }

    @Override
    public Long findLatestReviewId(Long enrollmentId) {
      return 1L;
    }

    @Override
    public String findLatestReviewSubmittedAt(Long enrollmentId) {
      return "2026-06-02 14:30:00";
    }
  }
}
