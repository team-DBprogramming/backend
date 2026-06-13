package com.example.backend.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfessorReviewMapper {

  void callGetProfessorReviews(Map<String, Object> params);
}
