package com.example.backend.dto.student;

public record StudentEnrollmentSummaryResponse(
    EnrollmentStatus enrollmentStatus, CreditSummary creditSummary, String serverTime) {

  public record EnrollmentStatus(
      String status,
      String startAt,
      String endAt,
      String deadline,
      Integer daysLeft,
      Long remainingSeconds) {}

  public record CreditSummary(Integer applied, Integer courseCount, Integer max, Integer remaining) {}
}
