package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.StudentHandler;
import com.example.backend.dto.student.StudentReviewListResponse;
import com.example.backend.dto.student.StudentReviewRequest;
import com.example.backend.dto.student.StudentReviewSubmitResponse;
import com.example.backend.mapper.StudentReviewMapper;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.utils.SemesterUtils;
import com.example.backend.utils.SemesterUtils.Semester;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentReviewService {

  private final StudentReviewMapper reviewMapper;
  private final Clock clock;

  public StudentReviewService(StudentReviewMapper reviewMapper, Clock clock) {
    this.reviewMapper = reviewMapper;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public StudentReviewListResponse getReviews(AuthenticatedUser currentUser, String semester) {
    String studentId = currentUser.requireStudentId();
    Semester targetSemester = SemesterUtils.parseOrCurrent(semester, LocalDate.now(clock));
    List<StudentReviewListResponse.Course> courses =
        reviewMapper.findReviews(studentId, targetSemester.year(), targetSemester.semester()).stream()
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
    String studentId = currentUser.requireStudentId();
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    params.put("courseId", courseId);
    params.put("rating", request.rating());
    params.put("difficulty", request.difficulty());
    params.put("comment", request.comment());
    reviewMapper.callInsertReview(params);
    if ("ENROLLMENT_NOT_FOUND".equals(params.get("result"))) {
      throw new StudentHandler(ErrorStatus.STUDENT_ENROLLMENT_NOT_FOUND);
    }
    return new StudentReviewSubmitResponse(
        String.valueOf(params.get("reviewId")),
        params.get("submittedAt") == null ? null : String.valueOf(params.get("submittedAt")));
  }
}
