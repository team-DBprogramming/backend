package com.example.backend.dto.student;

import java.util.List;

public record StudentDashboardResponse(
    StudentInfo studentInfo,
    StudentEnrollmentStatus enrollmentStatus,
    StudentCreditSummary creditSummary,
    List<StudentTodaySchedule> todaySchedule,
    StudentQuickActions quickActions) {}
