package com.example.backend.controller;

import com.example.backend.dto.professor.ProfessorStudentExportFile;
import com.example.backend.service.ProfessorStudentExportService;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/courses/{courseId}/students/export")
public class ProfessorStudentExportController {

  private final ProfessorStudentExportService exportService;

  public ProfessorStudentExportController(ProfessorStudentExportService exportService) {
    this.exportService = exportService;
  }

  @GetMapping
  public ResponseEntity<byte[]> exportStudents(
      @RequestHeader("Authorization") String authorization,
      @PathVariable String courseId,
      @RequestParam String format,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "grade", required = false) Integer grade,
      @RequestParam(value = "major", required = false) String major) {
    ProfessorStudentExportFile file =
        exportService.exportStudents(authorization, courseId, format, keyword, grade, major);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(file.filename()))
        .contentType(MediaType.parseMediaType(file.contentType()))
        .body(file.bytes());
  }

  private String contentDisposition(String filename) {
    return ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString();
  }
}
