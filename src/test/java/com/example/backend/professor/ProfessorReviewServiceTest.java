package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.backend.dto.professor.ProfessorReviewItem;
import com.example.backend.dto.professor.ProfessorReviewResponse;
import com.example.backend.mapper.ProfessorReviewMapper;
import com.example.backend.service.ProfessorReviewService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    reviewMapper.avgRating = 4.5;
    reviewMapper.participationRate = 50;
    reviewMapper.participantCount = 2;
    reviewMapper.itemOverall = 4.6;
    reviewMapper.itemContent = 4.5;
    reviewMapper.itemWorkload = 3.8;
    reviewMapper.itemKindness = 4.7;
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
    reviewMapper.participantCount = 0;

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
    assertThat(reviewMapper.requestedSort).isNull();
  }

  private static class FakeProfessorReviewMapper implements ProfessorReviewMapper {
    private String result = "SUCCESS";
    private Double avgRating;
    private Integer participationRate;
    private Integer participantCount;
    private Double itemOverall;
    private Double itemContent;
    private Double itemWorkload;
    private Double itemKindness;
    private final List<ProfessorReviewItem> reviews = new ArrayList<>();
    private Long requestedProfessorUserId;
    private String requestedCourseId;
    private String requestedDivision;
    private String requestedSemester;
    private String requestedSort;

    @Override
    public void callGetProfessorReviews(Map<String, Object> params) {
      requestedProfessorUserId = (Long) params.get("professorUserId");
      requestedCourseId = (String) params.get("courseId");
      requestedDivision = (String) params.get("division");
      requestedSemester = (String) params.get("semester");
      requestedSort = (String) params.get("sort");
      params.put("result", result);
      params.put("avgRating", avgRating);
      params.put("participationRate", participationRate);
      params.put("participantCount", participantCount);
      params.put("itemOverall", itemOverall);
      params.put("itemContent", itemContent);
      params.put("itemWorkload", itemWorkload);
      params.put("itemKindness", itemKindness);
      params.put("reviews", reviews);
    }
  }
}
