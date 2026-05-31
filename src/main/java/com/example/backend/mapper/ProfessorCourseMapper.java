package com.example.backend.mapper;

import com.example.backend.dto.professor.ProfessorCourseItem;
import com.example.backend.dto.professor.ProfessorCourseStatistics;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorCourseMapper {

  List<ProfessorCourseItem> findCourses(
      @Param("userId") Long userId,
      @Param("semester") String semester,
      @Param("keyword") String keyword);

  ProfessorCourseStatistics findStatistics(
      @Param("userId") Long userId, @Param("semester") String semester);
}
