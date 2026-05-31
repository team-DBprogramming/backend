package com.example.backend.dto.professor;

public record CourseRequestItem(
    String requestId,
    String studentId,
    String name,
    int grade,
    String major,
    String createdAt,
    String reason) {}
