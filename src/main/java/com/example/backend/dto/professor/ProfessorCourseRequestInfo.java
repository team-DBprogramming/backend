package com.example.backend.dto.professor;

public record ProfessorCourseRequestInfo(
    Long requestPk,
    String requestId,
    String status,
    Long studentUserId,
    Long professorUserId,
    Long sectionId,
    String courseName) {}
