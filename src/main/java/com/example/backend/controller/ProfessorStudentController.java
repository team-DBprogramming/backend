package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.dto.professor.ProfessorStudentListResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ProfessorStudentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/courses/{courseId}/students")
public class ProfessorStudentController {

  private final ProfessorStudentService studentService;

  public ProfessorStudentController(ProfessorStudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping
  public ApiResponse<ProfessorStudentListResponse> getStudents(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable String courseId,
      @RequestParam("division") String division,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "grade", required = false) Integer grade,
      @RequestParam(value = "major", required = false) String major,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_STUDENTS,
        studentService.getStudents(
            userDetails.toAuthenticatedUser(), courseId, division, keyword, grade, major, page, size));
  }
}
