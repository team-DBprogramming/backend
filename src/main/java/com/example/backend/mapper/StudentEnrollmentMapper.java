package com.example.backend.mapper;

import com.example.backend.dto.student.StudentCreditSummary;
import com.example.backend.dto.student.StudentEnrolledCourse;
import com.example.backend.dto.student.StudentEnrollmentStatus;
import com.example.backend.dto.student.StudentTimetableItem;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentEnrollmentMapper {

  Long findSectionId(@Param("courseId") String courseId, @Param("division") String division);

  void callInsertEnroll(Map<String, Object> params);

  void callCancelEnroll(Map<String, Object> params);

  void callGetEnrollStatus(Map<String, Object> params);

  void callGetCreditSummary(Map<String, Object> params);

  void callGetEnrolledCourses(Map<String, Object> params);

  void callGetEnrollmentTimetable(Map<String, Object> params);
}
