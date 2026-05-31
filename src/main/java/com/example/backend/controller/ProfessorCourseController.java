package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.config.OpenApiExamples;
import com.example.backend.dto.professor.ProfessorCourseListResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ProfessorCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/courses")
@Tag(name = "Professor Courses", description = "교수 담당 강의 API")
public class ProfessorCourseController {

  private final ProfessorCourseService courseService;

  public ProfessorCourseController(ProfessorCourseService courseService) {
    this.courseService = courseService;
  }

  @GetMapping
  @Operation(
      summary = "담당 강의 목록 조회",
      description = "로그인한 교수의 담당 강의를 학기와 강의명/학수번호 키워드로 조회합니다.")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "담당 강의 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.PROFESSOR_COURSES_RESPONSE)))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "인증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.ERROR_RESPONSE)))
  public ApiResponse<ProfessorCourseListResponse> getCourses(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "조회 학기", example = "2026-1")
          @RequestParam(value = "semester", required = false)
          String semester,
      @Parameter(description = "강의명 또는 학수번호 검색어", example = "데이터베이스")
          @RequestParam(value = "keyword", required = false)
          String keyword) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_COURSES,
        courseService.getCourses(userDetails.toAuthenticatedUser(), semester, keyword));
  }
}
