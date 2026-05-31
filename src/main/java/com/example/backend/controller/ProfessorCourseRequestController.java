package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.dto.professor.CourseRequestDecisionRequest;
import com.example.backend.dto.professor.CourseRequestDecisionResponse;
import com.example.backend.dto.professor.CourseRequestListResponse;
import com.example.backend.service.ProfessorCourseRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/courses/{courseId}/requests")
public class ProfessorCourseRequestController {

  private final ProfessorCourseRequestService requestService;

  public ProfessorCourseRequestController(ProfessorCourseRequestService requestService) {
    this.requestService = requestService;
  }

  @GetMapping
  public ApiResponse<CourseRequestListResponse> getRequests(
      @RequestHeader("Authorization") String authorization,
      @PathVariable String courseId,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_REQUESTS,
        requestService.getRequests(authorization, courseId, page, size));
  }

  @PatchMapping("/{requestId}")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<CourseRequestDecisionResponse> decideRequest(
      @RequestHeader("Authorization") String authorization,
      @PathVariable String courseId,
      @PathVariable String requestId,
      @RequestBody CourseRequestDecisionRequest request) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_REQUEST_PROCESSED,
        requestService.decideRequest(authorization, courseId, requestId, request));
  }
}
