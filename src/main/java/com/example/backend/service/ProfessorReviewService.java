package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorReviewItem;
import com.example.backend.dto.professor.ProfessorReviewResponse;
import com.example.backend.dto.professor.ProfessorReviewResponse.ItemAverages;
import com.example.backend.dto.professor.ProfessorReviewResponse.ReviewItem;
import com.example.backend.dto.professor.ProfessorReviewResponse.Summary;
import com.example.backend.mapper.ProfessorReviewMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorReviewService {

  private static final String RESULT_SUCCESS = "SUCCESS";
  private static final String RESULT_COURSE_NOT_FOUND = "COURSE_NOT_FOUND";
  private static final String RESULT_DIVISION_REQUIRED = "DIVISION_REQUIRED";

  private final ProfessorReviewMapper reviewMapper;

  public ProfessorReviewService(ProfessorReviewMapper reviewMapper) {
    this.reviewMapper = reviewMapper;
  }

  @Transactional(readOnly = true)
  public ProfessorReviewResponse getReviews(
      AuthenticatedUser currentUser, String courseId, String division, String semester, String sort) {
    Long professorUserId = currentUser.requireProfessorUserId();

    Map<String, Object> params = new HashMap<>();
    params.put("professorUserId", professorUserId);
    params.put("courseId", courseId);
    params.put("division", division);
    params.put("semester", semester);
    params.put("sort", sort);
    reviewMapper.callGetProfessorReviews(params);
    handleResult(stringValue(params.get("result")));

    List<ReviewItem> reviews =
        listValue(params.get("reviews")).stream()
            .map(this::toReviewItem)
            .toList();

    return new ProfessorReviewResponse(
        new Summary(
            valueOrDash(doubleObject(params.get("avgRating"))),
            valueOrDash(intObject(params.get("participationRate"))),
            intValue(intObject(params.get("participantCount")))),
        new ItemAverages(
            valueOrDash(doubleObject(params.get("itemOverall"))),
            valueOrDash(doubleObject(params.get("itemContent"))),
            valueOrDash(doubleObject(params.get("itemWorkload"))),
            valueOrDash(doubleObject(params.get("itemKindness")))),
        reviews);
  }

  private void handleResult(String result) {
    if (RESULT_SUCCESS.equals(result)) {
      return;
    }
    if (RESULT_DIVISION_REQUIRED.equals(result)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_DIVISION_REQUIRED);
    }
    if (RESULT_COURSE_NOT_FOUND.equals(result)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_COURSE_NOT_FOUND);
    }
    throw new ProfessorHandler(ErrorStatus.PROFESSOR_COURSE_NOT_FOUND);
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

  private Object valueOrDash(Object value) {
    return value == null ? "-" : value;
  }

  private int intValue(Integer value) {
    return value == null ? 0 : value;
  }

  private Integer intObject(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Number number) {
      return number.intValue();
    }
    return Integer.valueOf(String.valueOf(value));
  }

  private Double doubleObject(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Number number) {
      return number.doubleValue();
    }
    return Double.valueOf(String.valueOf(value));
  }

  private String stringValue(Object value) {
    return value == null ? null : String.valueOf(value);
  }

  @SuppressWarnings("unchecked")
  private List<ProfessorReviewItem> listValue(Object value) {
    return value instanceof List<?> list ? (List<ProfessorReviewItem>) list : List.of();
  }
}
