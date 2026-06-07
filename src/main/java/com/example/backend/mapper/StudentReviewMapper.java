package com.example.backend.mapper;

import com.example.backend.dto.student.StudentReviewItem;
import com.example.backend.dto.student.StudentReviewRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentReviewMapper {

  Long findStudentId(@Param("userId") Long userId);

  Long findEnrollmentId(@Param("studentId") Long studentId, @Param("courseId") String courseId);

  String findCurrentSemester();

  List<StudentReviewItem> findReviews(@Param("userId") Long userId, @Param("semester") String semester);

  void insertReview(@Param("enrollmentId") Long enrollmentId, @Param("request") StudentReviewRequest request);

  Long findLatestReviewId(@Param("enrollmentId") Long enrollmentId);

  String findLatestReviewSubmittedAt(@Param("enrollmentId") Long enrollmentId);
}
