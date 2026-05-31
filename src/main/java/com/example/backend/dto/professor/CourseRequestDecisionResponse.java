package com.example.backend.dto.professor;

import java.time.Instant;

public record CourseRequestDecisionResponse(String requestId, String status, Instant updatedAt) {}
