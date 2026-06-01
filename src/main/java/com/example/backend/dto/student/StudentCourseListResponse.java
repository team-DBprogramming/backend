package com.example.backend.dto.student;

import java.util.List;

public record StudentCourseListResponse(List<StudentCourseSummary> courses, Pagination pagination) {

  public record Pagination(Integer page, Integer size, Integer total) {}
}
