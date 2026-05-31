package com.example.backend.dto.professor;

public record ProfessorStudentSummary(
    String courseName,
    String courseId,
    String division,
    String semester,
    int totalStudents,
    int requestCount) {}
