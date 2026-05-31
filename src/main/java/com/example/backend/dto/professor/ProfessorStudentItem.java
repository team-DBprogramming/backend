package com.example.backend.dto.professor;

public record ProfessorStudentItem(
    String studentId, String name, int grade, String major, boolean isRetake) {}
