package com.example.backend.dto.professor;

public record ProfessorStudentExportRow(
    String studentId,
    String name,
    int grade,
    String major,
    String status,
    String enrolledAt,
    String note) {}
