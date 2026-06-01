package com.example.backend.dto.student;

public record StudentReviewRequest(
    Integer ratingOverall,
    Integer ratingContent,
    Integer ratingWorkload,
    Integer ratingProfessor,
    String difficulty,
    String pros,
    String cons,
    String advice,
    Boolean anonymous) {}
