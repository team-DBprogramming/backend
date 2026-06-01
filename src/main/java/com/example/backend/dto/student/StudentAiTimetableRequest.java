package com.example.backend.dto.student;

import java.util.List;

public record StudentAiTimetableRequest(Integer maxCredits, List<String> preferredDays, String keyword) {}
