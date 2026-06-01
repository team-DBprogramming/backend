package com.example.backend.dto.professor;

import java.util.List;

public record ProfessorMessageRequest(
    String courseId, String division, List<String> studentIds, String message) {}
