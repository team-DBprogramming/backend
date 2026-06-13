package com.example.backend.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfessorDashboardMapper {

  void callGetDashboardSummary(Map<String, Object> params);

  void callGetTodaySchedules(Map<String, Object> params);

  void callGetAssignedCourses(Map<String, Object> params);
}
