package com.example.backend.dto.student;

import java.util.List;

public record StudentBulkEnrollResponse(List<Result> results, Summary summary) {

  public record Result(String cartItemId, String courseId, boolean success, String message, String code) {}

  public record Summary(Integer total, Integer success, Integer failed) {}
}
