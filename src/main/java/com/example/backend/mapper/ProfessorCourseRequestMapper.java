package com.example.backend.mapper;

import com.example.backend.dto.professor.CourseRequestItem;
import com.example.backend.dto.professor.CourseRequestSummary;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorCourseRequestMapper {

  CourseRequestSummary findRequestSummary(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division);

  List<CourseRequestItem> findPendingRequests(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("page") Integer page,
      @Param("size") Integer size);

  void processCourseRequest(Map<String, Object> params);
}
