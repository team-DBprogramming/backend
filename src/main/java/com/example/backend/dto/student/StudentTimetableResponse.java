package com.example.backend.dto.student;

import java.util.List;

public record StudentTimetableResponse(List<StudentTimetableItem> schedules) {}
