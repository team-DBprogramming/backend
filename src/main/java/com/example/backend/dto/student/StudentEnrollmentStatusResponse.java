package com.example.backend.dto.student;

import java.util.List;

public record StudentEnrollmentStatusResponse(
    String periodStatus,
    Integer appliedCount,
    Integer appliedCredit,
    Integer remainingSlots,
    List<StudentEnrolledCourse> enrolledCourses) {}
