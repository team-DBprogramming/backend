package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.config.OpenApiExamples;
import com.example.backend.dto.professor.ProfessorReviewResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ProfessorReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/courses/{courseId}/reviews")
@Tag(name = "Professor Reviews", description = "교수 강의 평가 조회 API")
public class ProfessorReviewController {

  private final ProfessorReviewService reviewService;

  public ProfessorReviewController(ProfessorReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping
  @Operation(
      summary = "강의 평가 조회",
      description = "담당 강의 분반의 평가 요약, 항목별 평균, 익명 평가 목록을 조회합니다.")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "강의 평가 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.PROFESSOR_REVIEWS_RESPONSE)))
  public ApiResponse<ProfessorReviewResponse> getReviews(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @Parameter(description = "분반", required = true, example = "01") @RequestParam("division")
          String division,
      @Parameter(description = "조회 학기, 기본값 현재 학기", example = "2026-1")
          @RequestParam(value = "semester", required = false)
          String semester,
      @Parameter(description = "정렬 조건", example = "LATEST")
          @RequestParam(value = "sort", required = false)
          String sort) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_REVIEWS,
        reviewService.getReviews(userDetails.toAuthenticatedUser(), courseId, division, semester, sort));
  }
}
