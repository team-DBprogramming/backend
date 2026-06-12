package com.example.backend.mapper;

import com.example.backend.dto.student.StudentReviewItem;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentReviewMapper {

  List<StudentReviewItem> findReviews(
      @Param("studentId") String studentId,
      @Param("year") Integer year,
      @Param("semester") Integer semester);

  void callInsertReview(Map<String, Object> params);
}
