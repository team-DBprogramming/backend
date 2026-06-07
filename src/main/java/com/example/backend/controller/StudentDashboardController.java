package com.example.backend.controller;

import com.example.backend.dto.student.StudentApiResponse;
import com.example.backend.dto.student.StudentDashboardResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.StudentDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students/me")
@Tag(name = "Student Dashboard", description = "학생 대시보드 API")
public class StudentDashboardController {

  private final StudentDashboardService dashboardService;

  public StudentDashboardController(StudentDashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/dashboard")
  @Operation(
      summary = "학생 대시보드 정보 조회",
      description = "학생 정보, 수강신청 상태, 학점 현황, 오늘 시간표, 빠른 액션 정보를 조회합니다.")
  public StudentApiResponse<StudentDashboardResponse> getDashboard(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "조회 학기", example = "2026-1학기")
          @RequestParam(value = "semester", required = false)
          String semester) {
    return StudentApiResponse.success(
        "S200", "학생 대시보드 조회 성공", dashboardService.getDashboard(userDetails.toAuthenticatedUser(), semester));
  }
}
