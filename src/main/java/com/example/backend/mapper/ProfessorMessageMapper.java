package com.example.backend.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfessorMessageMapper {

  void sendProfessorMessage(Map<String, Object> params);
}
