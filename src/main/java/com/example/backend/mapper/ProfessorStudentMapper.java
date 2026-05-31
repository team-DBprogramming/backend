package com.example.backend.mapper;

import com.example.backend.dto.professor.ProfessorStudentItem;
import com.example.backend.dto.professor.ProfessorStudentSummary;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorStudentMapper {

  ProfessorStudentSummary findStudentSummary(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division);

  List<ProfessorStudentItem> findStudents(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("keyword") String keyword,
      @Param("grade") Integer grade,
      @Param("major") String major,
      @Param("size") int size,
      @Param("offset") int offset);
}
