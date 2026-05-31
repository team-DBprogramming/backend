package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.config.OpenApiExamples;
import com.example.backend.dto.professor.ProfessorStudentListResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ProfessorStudentService;
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
@RequestMapping("/professors/me/courses/{courseId}/students")
@Tag(name = "Professor Students", description = "교수 수강생 목록 API")
public class ProfessorStudentController {

  private final ProfessorStudentService studentService;

  public ProfessorStudentController(ProfessorStudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping
  @Operation(
      summary = "수강생 목록 조회",
      description = "담당 강의의 수강생 목록을 학번/이름 검색, 학년, 전공 조건으로 조회합니다.")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "수강생 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.PROFESSOR_STUDENTS_RESPONSE)))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "분반 누락",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "isSuccess": false,
                            "code": "PROFESSOR4005",
                            "message": "분반을 입력해주세요.",
                            "result": null
                          }
                          """)))
  public ApiResponse<ProfessorStudentListResponse> getStudents(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @Parameter(description = "분반", required = true, example = "01") @RequestParam("division")
          String division,
      @Parameter(description = "학번 또는 이름 검색어", example = "홍길동")
          @RequestParam(value = "keyword", required = false)
          String keyword,
      @Parameter(description = "학년 필터", example = "3")
          @RequestParam(value = "grade", required = false)
          Integer grade,
      @Parameter(description = "전공 필터", example = "컴퓨터공학과")
          @RequestParam(value = "major", required = false)
          String major,
      @Parameter(description = "페이지 번호, 기본값 1", example = "1")
          @RequestParam(value = "page", required = false)
          Integer page,
      @Parameter(description = "한 페이지당 수강생 수, 기본값 20", example = "20")
          @RequestParam(value = "size", required = false)
          Integer size) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_STUDENTS,
        studentService.getStudents(
            userDetails.toAuthenticatedUser(), courseId, division, keyword, grade, major, page, size));
  }
}
