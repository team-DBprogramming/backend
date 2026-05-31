package com.example.backend.dto.professor;

import java.util.List;

public record ProfessorStudentListResponse(
    ProfessorStudentSummary summary, List<ProfessorStudentItem> students) {}
