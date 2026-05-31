package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.config.OpenApiExamples;
import com.example.backend.dto.professor.CourseRequestDecisionRequest;
import com.example.backend.dto.professor.CourseRequestDecisionResponse;
import com.example.backend.dto.professor.CourseRequestListResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ProfessorCourseRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/courses/{courseId}/requests")
@Tag(name = "Professor Course Requests", description = "교수 빌넣 수강 요청 API")
public class ProfessorCourseRequestController {

  private final ProfessorCourseRequestService requestService;

  public ProfessorCourseRequestController(ProfessorCourseRequestService requestService) {
    this.requestService = requestService;
  }

  @GetMapping
  @Operation(
      summary = "빌넣 수강 요청 조회",
      description = "담당 강의의 승인 대기 중인 수강 요청 목록과 강의 요약 정보를 조회합니다.")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "수강 요청 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.COURSE_REQUESTS_RESPONSE)))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "인증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.ERROR_RESPONSE)))
  public ApiResponse<CourseRequestListResponse> getRequests(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @Parameter(description = "페이지 번호, 기본값 1", example = "1")
          @RequestParam(value = "page", required = false)
          Integer page,
      @Parameter(description = "한 페이지당 요청 수, 기본값 20", example = "20")
          @RequestParam(value = "size", required = false)
          Integer size) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_REQUESTS,
        requestService.getRequests(userDetails.toAuthenticatedUser(), courseId, page, size));
  }

  @PatchMapping("/{requestId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "빌넣 수강 요청 승인/거절",
      description = "담당 강의의 수강 요청 상태를 APPROVED 또는 REJECTED로 변경하고 대상 학생에게 알림을 생성합니다.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "승인/거절 요청",
              content =
                  @Content(
                      mediaType = "application/json",
                      examples =
                          @ExampleObject(value = OpenApiExamples.COURSE_REQUEST_DECISION_REQUEST))))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "201",
      description = "수강 요청 처리 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(value = OpenApiExamples.COURSE_REQUEST_DECISION_RESPONSE)))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "이미 처리된 요청 또는 잘못된 상태값",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "isSuccess": false,
                            "code": "PROFESSOR4001",
                            "message": "이미 승인 또는 거절 처리가 완료된 수강 요청입니다.",
                            "result": null
                          }
                          """)))
  public ApiResponse<CourseRequestDecisionResponse> decideRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @Parameter(description = "수강 요청 ID", example = "req-db-001") @PathVariable String requestId,
      @RequestBody CourseRequestDecisionRequest request) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_REQUEST_PROCESSED,
        requestService.decideRequest(
            userDetails.toAuthenticatedUser(), courseId, requestId, request));
  }
}
