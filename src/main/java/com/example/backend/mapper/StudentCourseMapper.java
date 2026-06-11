package com.example.backend.mapper;

import com.example.backend.dto.student.StudentCourseSchedule;
import com.example.backend.dto.student.StudentCourseReviewItem;
import com.example.backend.dto.student.StudentCourseSummary;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentCourseMapper {

  List<StudentCourseSummary> findCourses(
      @Param("keyword") String keyword,
      @Param("courseMajor") String courseMajor,
      @Param("courseType") String courseType,
      @Param("days") List<String> days,
      @Param("targetYear") Integer targetYear,
      @Param("isEnglish") Boolean isEnglish,
      @Param("credits") List<Integer> credits,
      @Param("startTime") String startTime,
      @Param("endTime") String endTime,
      @Param("hasSeatMargin") Boolean hasSeatMargin,
      @Param("highReview") Boolean highReview,
      @Param("sort") String sort,
      @Param("offset") Integer offset,
      @Param("size") Integer size);

  Integer countCourses(
      @Param("keyword") String keyword,
      @Param("courseMajor") String courseMajor,
      @Param("courseType") String courseType,
      @Param("days") List<String> days,
      @Param("targetYear") Integer targetYear,
      @Param("isEnglish") Boolean isEnglish,
      @Param("credits") List<Integer> credits,
      @Param("startTime") String startTime,
      @Param("endTime") String endTime,
      @Param("hasSeatMargin") Boolean hasSeatMargin,
      @Param("highReview") Boolean highReview);

  StudentCourseSummary findCourse(
      @Param("courseId") String courseId, @Param("division") String division);

  Map<String, Object> findCourseDetailFields(@Param("courseId") String courseId);

  String findDescription(@Param("courseId") String courseId);

  String findSyllabusUrl(@Param("courseId") String courseId, @Param("division") String division);

  String findProfessorEmail(@Param("courseId") String courseId, @Param("division") String division);

  String findProfessorPhone(@Param("courseId") String courseId, @Param("division") String division);

  String findPrerequisite(@Param("courseId") String courseId);

  List<StudentCourseSchedule> findSchedules(
      @Param("courseId") String courseId, @Param("division") String division);

  List<StudentCourseReviewItem> findCourseReviews(
      @Param("courseId") String courseId, @Param("division") String division);

  Long findSectionId(@Param("courseId") String courseId, @Param("division") String division);

  Integer countCurrentEnrollment(
      @Param("studentId") String studentId,
      @Param("courseId") String courseId,
      @Param("division") String division);

  String findBorrowRequestStatus(
      @Param("studentId") String studentId,
      @Param("courseId") String courseId,
      @Param("division") String division);

  void callInsertBorrowRequest(Map<String, Object> params);
}
