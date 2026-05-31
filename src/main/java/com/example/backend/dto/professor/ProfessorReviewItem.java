package com.example.backend.dto.professor;

public record ProfessorReviewItem(
    String reviewId,
    Double rating,
    String createdAt,
    String pros,
    String cons,
    String tip,
    String writer) {}
