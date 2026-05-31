package com.example.backend.service;

import com.example.backend.dto.professor.ProfessorCourseItem;
import com.example.backend.dto.professor.ProfessorCourseListResponse;
import com.example.backend.dto.professor.ProfessorCourseListResponse.CourseItem;
import com.example.backend.dto.professor.ProfessorCourseListResponse.Statistics;
import com.example.backend.dto.professor.ProfessorCourseStatistics;
import com.example.backend.mapper.ProfessorCourseMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorCourseService {

  private final ProfessorCourseMapper courseMapper;

  public ProfessorCourseService(ProfessorCourseMapper courseMapper) {
    this.courseMapper = courseMapper;
  }

  @Transactional(readOnly = true)
  public ProfessorCourseListResponse getCourses(
      AuthenticatedUser currentUser, String semester, String keyword) {
    Long professorUserId = currentUser.requireProfessorUserId();
    String normalizedSemester = normalize(semester);
    String normalizedKeyword = normalize(keyword);
    List<CourseItem> courses =
        courseMapper.findCourses(professorUserId, normalizedSemester, normalizedKeyword).stream()
            .map(this::toCourseItem)
            .toList();
    ProfessorCourseStatistics statistics =
        nullToEmptyStatistics(courseMapper.findStatistics(professorUserId, normalizedSemester));

    return new ProfessorCourseListResponse(
        courses,
        new Statistics(
            intValue(statistics.getTotalCourses()),
            intValue(statistics.getTotalStudents()),
            satisfactionValue(statistics.getAvgSatisfaction())));
  }

  private CourseItem toCourseItem(ProfessorCourseItem course) {
    return new CourseItem(
        course.getCourseId(),
        course.getCourseName(),
        course.getDivision(),
        intValue(course.getCredit()),
        valueOrEmpty(course.getSchedule()),
        valueOrEmpty(course.getRoom()),
        intValue(course.getCapacity()),
        intValue(course.getEnrolled()),
        satisfactionValue(course.getAvgSatisfaction()));
  }

  private ProfessorCourseStatistics nullToEmptyStatistics(ProfessorCourseStatistics statistics) {
    return statistics == null ? new ProfessorCourseStatistics(0, 0, null) : statistics;
  }

  private Object satisfactionValue(Double value) {
    return value == null ? "-" : value;
  }

  private int intValue(Integer value) {
    return value == null ? 0 : value;
  }

  private String valueOrEmpty(String value) {
    return value == null ? "" : value;
  }

  private String normalize(String value) {
    return isBlank(value) ? null : value.trim();
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
