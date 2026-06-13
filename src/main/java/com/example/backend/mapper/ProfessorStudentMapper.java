package com.example.backend.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfessorStudentMapper {

  void callGetProfessorStudentList(Map<String, Object> params);
}
