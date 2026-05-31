package com.example.backend.controller;

import com.example.backend.config.OpenApiExamples;
import com.example.backend.dto.professor.ProfessorStudentExportFile;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ProfessorStudentExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/courses/{courseId}/students/export")
@Tag(name = "Professor Student Export", description = "교수 수강생 명단 내보내기 API")
public class ProfessorStudentExportController {

  private final ProfessorStudentExportService exportService;

  public ProfessorStudentExportController(ProfessorStudentExportService exportService) {
    this.exportService = exportService;
  }

  @GetMapping
  @Operation(
      summary = "수강생 명단 내보내기",
      description = "담당 강의의 수강생 명단을 xlsx 또는 csv 파일로 다운로드합니다.")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "파일 다운로드 성공",
      content = {
        @Content(
            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            schema = @Schema(type = "string", format = "binary")),
        @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))
      })
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "지원하지 않는 다운로드 형식",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "isSuccess": false,
                            "code": "PROFESSOR4003",
                            "message": "다운로드 형식은 xlsx 또는 csv만 가능합니다.",
                            "result": null
                          }
                          """)))
  public ResponseEntity<byte[]> exportStudents(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @Parameter(description = "분반", required = true, example = "01") @RequestParam("division")
          String division,
      @Parameter(description = "다운로드 파일 형식", required = true, example = "xlsx")
          @RequestParam
          String format,
      @Parameter(description = "학번 또는 이름 검색어", example = "김")
          @RequestParam(value = "keyword", required = false)
          String keyword,
      @Parameter(description = "학년 필터", example = "3")
          @RequestParam(value = "grade", required = false)
          Integer grade,
      @Parameter(description = "전공 필터", example = "컴퓨터공학과")
          @RequestParam(value = "major", required = false)
          String major) {
    ProfessorStudentExportFile file =
        exportService.exportStudents(
            userDetails.toAuthenticatedUser(), courseId, division, format, keyword, grade, major);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(file.filename()))
        .contentType(MediaType.parseMediaType(file.contentType()))
        .body(file.bytes());
  }

  private String contentDisposition(String filename) {
    return ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString();
  }
}
