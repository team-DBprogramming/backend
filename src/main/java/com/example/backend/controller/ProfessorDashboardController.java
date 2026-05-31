package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.dto.professor.ProfessorDashboardResponse;
import com.example.backend.service.ProfessorDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me")
public class ProfessorDashboardController {

  private final ProfessorDashboardService dashboardService;

  public ProfessorDashboardController(ProfessorDashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/dashboard")
  public ApiResponse<ProfessorDashboardResponse> getDashboard(
      @RequestHeader("Authorization") String authorization,
      @RequestParam(value = "semester", required = false) String semester) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_DASHBOARD,
        dashboardService.getDashboard(authorization, semester));
  }
}
