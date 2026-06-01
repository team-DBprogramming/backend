package com.example.backend.dto.student;

public record StudentEnrollmentSummaryResponse(
    Integer appliedCredits, Integer maxCredits, Integer courseCount, Integer cartCount, Integer remainingCredits) {}
