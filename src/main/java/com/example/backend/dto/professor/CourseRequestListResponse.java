package com.example.backend.dto.professor;

import java.util.List;

public record CourseRequestListResponse(
    CourseRequestSummary summary, List<CourseRequestItem> requests) {}
