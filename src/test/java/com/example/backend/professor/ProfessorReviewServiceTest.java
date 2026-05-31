package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.backend.dto.professor.ProfessorReviewItem;
import com.example.backend.dto.professor.ProfessorReviewItemAverages;
import com.example.backend.dto.professor.ProfessorReviewResponse;
import com.example.backend.dto.professor.ProfessorReviewSummary;
import com.example.backend.mapper.ProfessorReviewMapper;
import com.example.backend.service.ProfessorReviewService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorReviewServiceTest {

  private FakeProfessorReviewMapper reviewMapper;
  private ProfessorReviewService reviewService;

  @BeforeEach
  void setUp() {
    reviewMapper = new FakeProfessorReviewMapper();
    reviewService = new ProfessorReviewService(reviewMapper);
  }

  @Test
  void getReviewsReturnsSummaryItemAveragesAndAnonymousReviews() {
    reviewMapper.summary = new ProfessorReviewSummary(4.5, 2, 4);
    reviewMapper.itemAverages = new ProfessorReviewItemAverages(4.6, 4.5, 3.8, 4.7);
    reviewMapper.reviews.add(
        new ProfessorReviewItem(
            "REV_001", 5.0, "2026.05.10", "Good practice", "Many assignments", "Prepare SQL", "익명"));
    reviewMapper.reviews.add(
        new ProfessorReviewItem("REV_002", 4.0, "2026.05.09", "Clear examples", null, null, "익명"));

    ProfessorReviewResponse response =
        reviewService.getReviews(professorUser(), "CSE301", "01", "2026-1", "RATING_DESC");

    assertThat(response.summary().avgRating()).isEqualTo(4.5);
    assertThat(response.summary().participationRate()).isEqualTo(50);
    assertThat(response.summary().participantCount()).isEqualTo(2);
    assertThat(response.itemAverages().overall()).isEqualTo(4.6);
    assertThat(response.itemAverages().content()).isEqualTo(4.5);
    assertThat(response.itemAverages().workload()).isEqualTo(3.8);
    assertThat(response.itemAverages().kindness()).isEqualTo(4.7);
    assertThat(response.reviews()).hasSize(2);
    assertThat(response.reviews().get(0).writer()).isEqualTo("익명");
    assertThat(reviewMapper.requestedProfessorUserId).isEqualTo(10L);
    assertThat(reviewMapper.requestedCourseId).isEqualTo("CSE301");
    assertThat(reviewMapper.requestedDivision).isEqualTo("01");
    assertThat(reviewMapper.requestedSemester).isEqualTo("2026-1");
    assertThat(reviewMapper.requestedSort).isEqualTo("RATING_DESC");
  }

  @Test
  void getReviewsReturnsDashWhenReviewDataIsEmpty() {
    reviewMapper.summary = new ProfessorReviewSummary(null, 0, 0);
    reviewMapper.itemAverages = new ProfessorReviewItemAverages(null, null, null, null);

    ProfessorReviewResponse response =
        reviewService.getReviews(professorUser(), "CSE301", "01", null, null);

    assertThat(response.summary().avgRating()).isEqualTo("-");
    assertThat(response.summary().participationRate()).isEqualTo("-");
    assertThat(response.summary().participantCount()).isZero();
    assertThat(response.itemAverages().overall()).isEqualTo("-");
    assertThat(response.itemAverages().content()).isEqualTo("-");
    assertThat(response.itemAverages().workload()).isEqualTo("-");
    assertThat(response.itemAverages().kindness()).isEqualTo("-");
    assertThat(response.reviews()).isEmpty();
    assertThat(reviewMapper.requestedSort).isEqualTo("LATEST");
  }

  private static class FakeProfessorReviewMapper implements ProfessorReviewMapper {
    private ProfessorReviewSummary summary;
    private ProfessorReviewItemAverages itemAverages;
    private final List<ProfessorReviewItem> reviews = new ArrayList<>();
    private Long requestedProfessorUserId;
    private String requestedCourseId;
    private String requestedDivision;
    private String requestedSemester;
    private String requestedSort;

    @Override
    public int existsCourse(Long professorUserId, String courseId, String division, String semester) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      requestedDivision = division;
      requestedSemester = semester;
      return 1;
    }

    @Override
    public ProfessorReviewSummary findSummary(
        Long professorUserId, String courseId, String division, String semester) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      requestedDivision = division;
      requestedSemester = semester;
      return summary;
    }

    @Override
    public ProfessorReviewItemAverages findItemAverages(
        Long professorUserId, String courseId, String division, String semester) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      requestedDivision = division;
      requestedSemester = semester;
      return itemAverages;
    }

    @Override
    public List<ProfessorReviewItem> findReviews(
        Long professorUserId, String courseId, String division, String semester, String sort) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      requestedDivision = division;
      requestedSemester = semester;
      requestedSort = sort;
      return reviews;
    }
  }
}
