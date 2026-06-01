package com.example.backend.service;

import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.dto.student.StudentReviewListResponse;
import com.example.backend.dto.student.StudentReviewRequest;
import com.example.backend.mapper.StudentReviewMapper;
import com.example.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentReviewService {

  private final StudentReviewMapper reviewMapper;

  public StudentReviewService(StudentReviewMapper reviewMapper) {
    this.reviewMapper = reviewMapper;
  }

  @Transactional(readOnly = true)
  public StudentReviewListResponse getReviews(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    return new StudentReviewListResponse(reviewMapper.findReviews(userId));
  }

  @Transactional
  public StudentMutationResponse submitReview(
      AuthenticatedUser currentUser, String courseId, StudentReviewRequest request) {
    Long studentId = reviewMapper.findStudentId(currentUser.requireStudentUserId());
    Long enrollmentId = reviewMapper.findEnrollmentId(studentId, courseId);
    reviewMapper.insertReview(enrollmentId, request);
    Long reviewId = reviewMapper.findLatestReviewId(enrollmentId);
    return new StudentMutationResponse(String.valueOf(reviewId), "SUBMITTED");
  }
}
