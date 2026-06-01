package com.example.backend.mapper;

import com.example.backend.dto.student.StudentCreditSummary;
import com.example.backend.dto.student.StudentEnrolledCourse;
import com.example.backend.dto.student.StudentEnrollmentStatus;
import com.example.backend.dto.student.StudentTimetableItem;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentEnrollmentMapper {

  Long findStudentId(@Param("userId") Long userId);

  Long findSectionId(@Param("courseId") String courseId, @Param("division") String division);

  void insertEnrollment(@Param("studentId") Long studentId, @Param("sectionId") Long sectionId);

  int cancelEnrollment(@Param("studentId") Long studentId, @Param("courseId") String courseId);

  int increaseEnrolledCount(@Param("sectionId") Long sectionId);

  int decreaseEnrolledCount(@Param("studentId") Long studentId, @Param("courseId") String courseId);

  StudentEnrollmentStatus findEnrollmentStatus(@Param("userId") Long userId);

  StudentCreditSummary findCreditSummary(
      @Param("userId") Long userId, @Param("maxCredits") Integer maxCredits);

  List<StudentEnrolledCourse> findEnrolledCourses(@Param("userId") Long userId);

  List<StudentTimetableItem> findEnrollmentTimetable(@Param("userId") Long userId);
}
