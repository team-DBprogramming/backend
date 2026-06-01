package com.example.backend.service;

import com.example.backend.dto.student.StudentCreditSummary;
import com.example.backend.dto.student.StudentEnrollmentRequest;
import com.example.backend.dto.student.StudentEnrollmentStatus;
import com.example.backend.dto.student.StudentEnrollmentStatusResponse;
import com.example.backend.dto.student.StudentEnrollmentSummaryResponse;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.dto.student.StudentTimetableResponse;
import com.example.backend.mapper.StudentEnrollmentMapper;
import com.example.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentEnrollmentService {

  private static final int MAX_CREDITS = 18;

  private final StudentEnrollmentMapper enrollmentMapper;

  public StudentEnrollmentService(StudentEnrollmentMapper enrollmentMapper) {
    this.enrollmentMapper = enrollmentMapper;
  }

  @Transactional
  public StudentMutationResponse enroll(AuthenticatedUser currentUser, StudentEnrollmentRequest request) {
    Long studentId = enrollmentMapper.findStudentId(currentUser.requireStudentUserId());
    Long sectionId = enrollmentMapper.findSectionId(request.courseId(), normalizeDivision(request.division()));
    enrollmentMapper.insertEnrollment(studentId, sectionId);
    enrollmentMapper.increaseEnrolledCount(sectionId);
    return new StudentMutationResponse(String.valueOf(sectionId), "ENROLLED");
  }

  @Transactional
  public StudentMutationResponse cancel(AuthenticatedUser currentUser, String courseId) {
    Long studentId = enrollmentMapper.findStudentId(currentUser.requireStudentUserId());
    enrollmentMapper.decreaseEnrolledCount(studentId, courseId);
    enrollmentMapper.cancelEnrollment(studentId, courseId);
    return new StudentMutationResponse(courseId, "DROPPED");
  }

  @Transactional(readOnly = true)
  public StudentEnrollmentStatusResponse getStatus(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    StudentEnrollmentStatus status = enrollmentMapper.findEnrollmentStatus(userId);
    if (status == null) {
      return new StudentEnrollmentStatusResponse("CLOSED", null, 0);
    }
    return new StudentEnrollmentStatusResponse(status.getStatus(), status.getDeadline(), status.getDaysLeft());
  }

  @Transactional(readOnly = true)
  public StudentEnrollmentSummaryResponse getSummary(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    StudentCreditSummary summary = enrollmentMapper.findCreditSummary(userId, MAX_CREDITS);
    int applied = summary == null || summary.getApplied() == null ? 0 : summary.getApplied();
    int max = summary == null || summary.getMax() == null ? MAX_CREDITS : summary.getMax();
    int courseCount = summary == null || summary.getCourseCount() == null ? 0 : summary.getCourseCount();
    int cartCount = summary == null || summary.getCartCount() == null ? 0 : summary.getCartCount();
    return new StudentEnrollmentSummaryResponse(applied, max, courseCount, cartCount, Math.max(0, max - applied));
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse getPreview(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    return new StudentTimetableResponse(enrollmentMapper.findEnrollmentTimetable(userId));
  }

  private String normalizeDivision(String division) {
    return division == null || division.trim().isEmpty() ? null : division.replace("분반", "").trim();
  }
}
