package com.example.backend.mapper;

import com.example.backend.dto.professor.ProfessorStudentExportCourse;
import com.example.backend.dto.professor.ProfessorStudentExportRow;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorStudentExportMapper {

  ProfessorStudentExportCourse findCourse(
      @Param("professorUserId") Long professorUserId, @Param("courseId") String courseId);

  List<ProfessorStudentExportRow> findStudents(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("keyword") String keyword,
      @Param("grade") Integer grade,
      @Param("major") String major);
}
