package com.example.backend.controller;

import com.example.backend.dto.student.StudentApiResponse;
import com.example.backend.dto.student.StudentEnrollmentRequest;
import com.example.backend.dto.student.StudentEnrollmentStatusResponse;
import com.example.backend.dto.student.StudentEnrollmentSummaryResponse;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.dto.student.StudentTimetableResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.StudentEnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollment")
@Tag(name = "Student Enrollment", description = "학생 실시간 수강신청 API")
public class StudentEnrollmentController {

  private final StudentEnrollmentService enrollmentService;

  public StudentEnrollmentController(StudentEnrollmentService enrollmentService) {
    this.enrollmentService = enrollmentService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "수강신청", description = "강의를 수강신청합니다.")
  public StudentApiResponse<StudentMutationResponse> enroll(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody StudentEnrollmentRequest request) {
    return StudentApiResponse.success(
        "S201", "수강신청 성공", enrollmentService.enroll(userDetails.toAuthenticatedUser(), request));
  }

  @DeleteMapping("/{courseId}")
  @Operation(summary = "수강취소", description = "강의 코드 기준으로 현재 학기 수강신청을 취소합니다.")
  public StudentApiResponse<StudentMutationResponse> cancel(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable String courseId) {
    return StudentApiResponse.success(
        "S200", "수강취소 성공", enrollmentService.cancel(userDetails.toAuthenticatedUser(), courseId));
  }

  @GetMapping("/status")
  @Operation(summary = "신청 상태", description = "현재 수강신청 기간 상태를 조회합니다.")
  public StudentApiResponse<StudentEnrollmentStatusResponse> getStatus(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
    return StudentApiResponse.success(
        "S200", "수강신청 상태 조회 성공", enrollmentService.getStatus(userDetails.toAuthenticatedUser()));
  }

  @GetMapping("/summary")
  @Operation(summary = "수강신청 대시보드", description = "현재 신청 학점과 신청 강의 요약을 조회합니다.")
  public StudentApiResponse<StudentEnrollmentSummaryResponse> getSummary(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
    return StudentApiResponse.success(
        "S200", "수강신청 요약 조회 성공", enrollmentService.getSummary(userDetails.toAuthenticatedUser()));
  }

  @GetMapping("/preview")
  @Operation(summary = "시간표 미리보기", description = "현재 수강신청된 강의 기준 시간표를 미리 조회합니다.")
  public StudentApiResponse<StudentTimetableResponse> getPreview(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
    return StudentApiResponse.success(
        "S200", "시간표 미리보기 조회 성공", enrollmentService.getPreview(userDetails.toAuthenticatedUser()));
  }
}
