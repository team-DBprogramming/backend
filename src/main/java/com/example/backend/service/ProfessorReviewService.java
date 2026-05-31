package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorReviewItem;
import com.example.backend.dto.professor.ProfessorReviewItemAverages;
import com.example.backend.dto.professor.ProfessorReviewResponse;
import com.example.backend.dto.professor.ProfessorReviewResponse.ItemAverages;
import com.example.backend.dto.professor.ProfessorReviewResponse.ReviewItem;
import com.example.backend.dto.professor.ProfessorReviewResponse.Summary;
import com.example.backend.dto.professor.ProfessorReviewSummary;
import com.example.backend.mapper.ProfessorReviewMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorReviewService {

  private static final String DEFAULT_SORT = "LATEST";

  private final ProfessorReviewMapper reviewMapper;

  public ProfessorReviewService(ProfessorReviewMapper reviewMapper) {
    this.reviewMapper = reviewMapper;
  }

  @Transactional(readOnly = true)
  public ProfessorReviewResponse getReviews(
      AuthenticatedUser currentUser, String courseId, String division, String semester, String sort) {
    Long professorUserId = currentUser.requireProfessorUserId();
    String normalizedDivision = normalizeRequiredDivision(division);
    String normalizedSemester = normalize(semester);
    String normalizedSort = normalizeSort(sort);

    if (reviewMapper.existsCourse(professorUserId, courseId, normalizedDivision, normalizedSemester)
        <= 0) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_COURSE_NOT_FOUND);
    }

    ProfessorReviewSummary summary =
        nullToEmptySummary(
            reviewMapper.findSummary(professorUserId, courseId, normalizedDivision, normalizedSemester));
    ProfessorReviewItemAverages itemAverages =
        nullToEmptyItemAverages(
            reviewMapper.findItemAverages(
                professorUserId, courseId, normalizedDivision, normalizedSemester));
    List<ReviewItem> reviews =
        reviewMapper
            .findReviews(professorUserId, courseId, normalizedDivision, normalizedSemester, normalizedSort)
            .stream()
            .map(this::toReviewItem)
            .toList();

    return new ProfessorReviewResponse(
        new Summary(
            ratingValue(summary.avgRating()),
            participationRate(summary.participantCount(), summary.totalStudents()),
            intValue(summary.participantCount())),
        new ItemAverages(
            ratingValue(itemAverages.overall()),
            ratingValue(itemAverages.content()),
            ratingValue(itemAverages.workload()),
            ratingValue(itemAverages.kindness())),
        reviews);
  }

  private ReviewItem toReviewItem(ProfessorReviewItem review) {
    return new ReviewItem(
        review.reviewId(),
        review.rating() == null ? 0.0 : review.rating(),
        review.createdAt(),
        review.pros(),
        review.cons(),
        review.tip(),
        review.writer());
  }

  private ProfessorReviewSummary nullToEmptySummary(ProfessorReviewSummary summary) {
    return summary == null ? new ProfessorReviewSummary(null, 0, 0) : summary;
  }

  private ProfessorReviewItemAverages nullToEmptyItemAverages(
      ProfessorReviewItemAverages itemAverages) {
    return itemAverages == null
        ? new ProfessorReviewItemAverages(null, null, null, null)
        : itemAverages;
  }

  private Object participationRate(Integer participantCount, Integer totalStudents) {
    int participants = intValue(participantCount);
    int students = intValue(totalStudents);
    if (students <= 0) {
      return "-";
    }
    return (int) Math.round(participants * 100.0 / students);
  }

  private Object ratingValue(Double value) {
    return value == null ? "-" : value;
  }

  private int intValue(Integer value) {
    return value == null ? 0 : value;
  }

  private String normalizeRequiredDivision(String division) {
    if (isBlank(division)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_DIVISION_REQUIRED);
    }
    return division.trim();
  }

  private String normalizeSort(String sort) {
    String normalized = normalize(sort);
    if (normalized == null) {
      return DEFAULT_SORT;
    }
    return switch (normalized) {
      case "LATEST", "RATING_DESC", "RATING_ASC" -> normalized;
      default -> DEFAULT_SORT;
    };
  }

  private String normalize(String value) {
    return isBlank(value) ? null : value.trim();
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
