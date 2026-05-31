package com.example.backend.dto.professor;

public record CourseRequestSummary(
    String courseName,
    String courseId,
    String division,
    String semester,
    int totalStudents,
    int requestCount) {}
