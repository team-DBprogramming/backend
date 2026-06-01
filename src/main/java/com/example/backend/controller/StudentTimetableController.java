package com.example.backend.controller;

import com.example.backend.dto.student.StudentAiTimetableRequest;
import com.example.backend.dto.student.StudentApiResponse;
import com.example.backend.dto.student.StudentCartExportRequest;
import com.example.backend.dto.student.StudentTimetableResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.StudentTimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Student Timetable", description = "학생 시간표 및 AI 추천 API")
public class StudentTimetableController {

  private final StudentTimetableService timetableService;

  public StudentTimetableController(StudentTimetableService timetableService) {
    this.timetableService = timetableService;
  }

  @GetMapping("/students/me/timetable")
  @Operation(summary = "내 시간표 조회", description = "현재 로그인한 학생의 현재 학기 시간표를 조회합니다.")
  public StudentApiResponse<StudentTimetableResponse> getTimetable(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
    return StudentApiResponse.success(
        "S200", "내 시간표 조회 성공", timetableService.getTimetable(userDetails.toAuthenticatedUser()));
  }

  @PostMapping("/students/me/timetable/export-cart")
  @Operation(summary = "장바구니 시간표 내보내기", description = "장바구니 강의 기준 시간표를 조회합니다.")
  public StudentApiResponse<StudentTimetableResponse> exportCart(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody(required = false) StudentCartExportRequest request) {
    return StudentApiResponse.success(
        "S200", "장바구니 시간표 내보내기 성공", timetableService.exportCart(userDetails.toAuthenticatedUser()));
  }

  @PostMapping("/timetable/ai-recommend")
  @Operation(summary = "AI 시간표 추천", description = "선호 조건을 바탕으로 추천 시간표 후보를 조회합니다.")
  public StudentApiResponse<StudentTimetableResponse> recommend(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody(required = false) StudentAiTimetableRequest request) {
    return StudentApiResponse.success(
        "S200", "AI 시간표 추천 성공", timetableService.recommend(userDetails.toAuthenticatedUser(), request));
  }
}
