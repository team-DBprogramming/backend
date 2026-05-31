package com.example.backend.mapper;

import com.example.backend.dto.professor.ProfessorAssignedCourse;
import com.example.backend.dto.professor.ProfessorDashboardSummary;
import com.example.backend.dto.professor.ProfessorTodaySchedule;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorDashboardMapper {

  ProfessorDashboardSummary findDashboardSummary(
      @Param("userId") Long userId, @Param("semester") String semester);

  List<ProfessorTodaySchedule> findTodaySchedules(
      @Param("userId") Long userId,
      @Param("semester") String semester,
      @Param("dayOfWeek") String dayOfWeek);

  List<ProfessorAssignedCourse> findAssignedCourses(
      @Param("userId") Long userId, @Param("semester") String semester);
}
