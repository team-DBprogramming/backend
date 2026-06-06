package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.StudentHandler;
import com.example.backend.dto.student.StudentReviewListResponse;
import com.example.backend.dto.student.StudentReviewRequest;
import com.example.backend.dto.student.StudentReviewSubmitResponse;
import com.example.backend.mapper.StudentReviewMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentReviewService {

  private final StudentReviewMapper reviewMapper;

  public StudentReviewService(StudentReviewMapper reviewMapper) {
    this.reviewMapper = reviewMapper;
  }

  @Transactional(readOnly = true)
  public StudentReviewListResponse getReviews(AuthenticatedUser currentUser, String semester) {
    Long userId = currentUser.requireStudentUserId();
    List<StudentReviewListResponse.Course> courses =
        reviewMapper.findReviews(userId, normalize(semester)).stream()
            .map(
                review ->
                    new StudentReviewListResponse.Course(
                        review.getCourseId(),
                        review.getCourseName(),
                        review.getProfessor(),
                        review.getSemester(),
                        review.getCredit(),
                        "SUBMITTED".equals(review.getStatus())))
            .toList();
    long completed = courses.stream().filter(StudentReviewListResponse.Course::isCompleted).count();
    return new StudentReviewListResponse(
        courses, new StudentReviewListResponse.Summary((int) completed, courses.size() - (int) completed));
  }

  @Transactional
  public StudentReviewSubmitResponse submitReview(
      AuthenticatedUser currentUser, String courseId, StudentReviewRequest request) {
    Long userId = currentUser.requireStudentUserId();
    Long studentId = reviewMapper.findStudentId(userId);
    if (studentId == null) {
      throw new StudentHandler(ErrorStatus.STUDENT_NOT_FOUND);
    }

    Long enrollmentId = reviewMapper.findEnrollmentId(studentId, courseId);
    if (enrollmentId == null) {
      throw new StudentHandler(ErrorStatus.STUDENT_ENROLLMENT_NOT_FOUND);
    }

    reviewMapper.insertReview(enrollmentId, request);
    Long reviewId = reviewMapper.findLatestReviewId(enrollmentId);
    String submittedAt = reviewMapper.findLatestReviewSubmittedAt(enrollmentId);
    return new StudentReviewSubmitResponse(String.valueOf(reviewId), submittedAt);
  }

  private String normalize(String value) {
    return value == null || value.trim().isEmpty() ? null : value.trim();
  }
}
