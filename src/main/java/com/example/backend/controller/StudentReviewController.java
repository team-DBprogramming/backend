package com.example.backend.controller;

import com.example.backend.dto.student.StudentApiResponse;
import com.example.backend.dto.student.StudentReviewListResponse;
import com.example.backend.dto.student.StudentReviewRequest;
import com.example.backend.dto.student.StudentReviewSubmitResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.StudentReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Student Reviews", description = "학생 강의평가 API")
public class StudentReviewController {

  private final StudentReviewService reviewService;

  public StudentReviewController(StudentReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping("/students/me/reviews")
  @Operation(summary = "내 강의평가 목록", description = "현재 로그인한 학생의 강의평가 제출/미제출 목록을 조회합니다.")
  public StudentApiResponse<StudentReviewListResponse> getReviews(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "조회 학기", example = "2026-1")
          @RequestParam(value = "semester", required = false)
          String semester) {
    return StudentApiResponse.success(
        "S200", "강의평가 목록 조회 성공", reviewService.getReviews(userDetails.toAuthenticatedUser(), semester));
  }

  @PostMapping("/courses/{courseId}/reviews")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "강의평가 제출", description = "현재 학기 수강 강의에 강의평가를 제출합니다.")
  public StudentApiResponse<StudentReviewSubmitResponse> submitReview(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable String courseId,
      @RequestBody StudentReviewRequest request) {
    return StudentApiResponse.success(
        "S201", "강의평가 제출 성공", reviewService.submitReview(userDetails.toAuthenticatedUser(), courseId, request));
  }
}
