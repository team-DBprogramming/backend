package com.example.backend.mapper;

import com.example.backend.dto.student.StudentCourseSchedule;
import com.example.backend.dto.student.StudentCourseReviewItem;
import com.example.backend.dto.student.StudentCourseSummary;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentCourseMapper {

  List<StudentCourseSummary> findCourses(
      @Param("semester") String semester,
      @Param("keyword") String keyword,
      @Param("courseCategory") String courseCategory,
      @Param("major") String major,
      @Param("courseType") String courseType,
      @Param("days") List<String> days,
      @Param("credits") List<Integer> credits,
      @Param("startTime") String startTime,
      @Param("endTime") String endTime,
      @Param("sort") String sort,
      @Param("offset") Integer offset,
      @Param("size") Integer size);

  Integer countCourses(
      @Param("semester") String semester,
      @Param("keyword") String keyword,
      @Param("courseCategory") String courseCategory,
      @Param("major") String major,
      @Param("courseType") String courseType,
      @Param("days") List<String> days,
      @Param("credits") List<Integer> credits,
      @Param("startTime") String startTime,
      @Param("endTime") String endTime);

  StudentCourseSummary findCourse(
      @Param("courseId") String courseId, @Param("division") String division);

  String findDescription(@Param("courseId") String courseId);

  String findSyllabusUrl(@Param("courseId") String courseId, @Param("division") String division);

  String findProfessorEmail(@Param("courseId") String courseId, @Param("division") String division);

  String findProfessorPhone(@Param("courseId") String courseId, @Param("division") String division);

  String findPrerequisite(@Param("courseId") String courseId);

  List<StudentCourseSchedule> findSchedules(
      @Param("courseId") String courseId, @Param("division") String division);

  List<StudentCourseReviewItem> findCourseReviews(
      @Param("courseId") String courseId, @Param("division") String division);

  Long findStudentId(@Param("userId") Long userId);

  Long findSectionId(@Param("courseId") String courseId, @Param("division") String division);

  void insertBorrowRequest(
      @Param("studentId") Long studentId,
      @Param("sectionId") Long sectionId,
      @Param("reason") String reason);

  Long findLatestBorrowRequestId(@Param("studentId") Long studentId, @Param("sectionId") Long sectionId);
}
