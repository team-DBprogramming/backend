package com.example.backend.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfessorStudentExportMapper {

  void callGetExportCourse(Map<String, Object> params);

  void callGetExportStudents(Map<String, Object> params);
}
