package com.example.backend.mapper;

import com.example.backend.dto.professor.ProfessorReviewItem;
import com.example.backend.dto.professor.ProfessorReviewItemAverages;
import com.example.backend.dto.professor.ProfessorReviewSummary;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorReviewMapper {

  int existsCourse(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("semester") String semester);

  ProfessorReviewSummary findSummary(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("semester") String semester);

  ProfessorReviewItemAverages findItemAverages(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("semester") String semester);

  List<ProfessorReviewItem> findReviews(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("semester") String semester,
      @Param("sort") String sort);
}
