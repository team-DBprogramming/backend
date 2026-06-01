package com.example.backend.dto.student;

public record StudentReviewRequest(
    Integer rating,
    Integer difficulty,
    String comment) {}
