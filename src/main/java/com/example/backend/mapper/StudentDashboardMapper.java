package com.example.backend.mapper;

import com.example.backend.dto.student.StudentCreditSummary;
import com.example.backend.dto.student.StudentEnrollmentStatus;
import com.example.backend.dto.student.StudentInfo;
import com.example.backend.dto.student.StudentQuickActions;
import com.example.backend.dto.student.StudentTodaySchedule;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentDashboardMapper {

  StudentInfo findStudentInfo(@Param("studentId") String studentId, @Param("semesterLabel") String semesterLabel);

  void callGetEnrollStatus(Map<String, Object> params);

  void callGetCreditSummary(Map<String, Object> params);

  List<StudentTodaySchedule> findTodaySchedules(
      @Param("studentId") String studentId,
      @Param("dayOfWeek") String dayOfWeek,
      @Param("year") Integer year,
      @Param("semester") Integer semester);

  StudentQuickActions findQuickActions(
      @Param("studentId") String studentId,
      @Param("year") Integer year,
      @Param("semester") Integer semester);
}
