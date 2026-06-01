package com.example.backend.mapper;

import com.example.backend.dto.student.StudentCreditSummary;
import com.example.backend.dto.student.StudentEnrollmentStatus;
import com.example.backend.dto.student.StudentInfo;
import com.example.backend.dto.student.StudentQuickActions;
import com.example.backend.dto.student.StudentTodaySchedule;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentDashboardMapper {

  StudentInfo findStudentInfo(@Param("userId") Long userId);

  StudentEnrollmentStatus findEnrollmentStatus(@Param("userId") Long userId);

  StudentCreditSummary findCreditSummary(
      @Param("userId") Long userId, @Param("maxCredits") Integer maxCredits);

  List<StudentTodaySchedule> findTodaySchedules(
      @Param("userId") Long userId, @Param("dayOfWeek") String dayOfWeek);

  StudentQuickActions findQuickActions(@Param("userId") Long userId);
}
