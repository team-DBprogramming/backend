package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.dto.professor.ProfessorCourseListResponse;
import com.example.backend.service.ProfessorCourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/courses")
public class ProfessorCourseController {

  private final ProfessorCourseService courseService;

  public ProfessorCourseController(ProfessorCourseService courseService) {
    this.courseService = courseService;
  }

  @GetMapping
  public ApiResponse<ProfessorCourseListResponse> getCourses(
      @RequestHeader("Authorization") String authorization,
      @RequestParam(value = "semester", required = false) String semester,
      @RequestParam(value = "keyword", required = false) String keyword) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_COURSES,
        courseService.getCourses(authorization, semester, keyword));
  }
}
