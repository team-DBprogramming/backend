package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.config.OpenApiExamples;
import com.example.backend.dto.professor.ProfessorDashboardResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ProfessorDashboardService;
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
@RequestMapping("/professors/me")
@Tag(name = "Professor Dashboard", description = "교수 대시보드 API")
public class ProfessorDashboardController {

  private final ProfessorDashboardService dashboardService;

  public ProfessorDashboardController(ProfessorDashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/dashboard")
  @Operation(
      summary = "교수 대시보드 조회",
      description = "로그인한 교수의 담당 강의 수, 총 수강생 수, 평균 만족도, 신규 평가 수, 오늘 강의와 담당 강의 목록을 조회합니다.")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "교수 대시보드 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.PROFESSOR_DASHBOARD_RESPONSE)))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "인증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.ERROR_RESPONSE)))
  public ApiResponse<ProfessorDashboardResponse> getDashboard(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "조회 학기", example = "2026-1")
          @RequestParam(value = "semester", required = false)
          String semester) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_DASHBOARD,
        dashboardService.getDashboard(userDetails.toAuthenticatedUser(), semester));
  }
}
