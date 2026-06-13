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
    List<CourseItem> courses =
        courseMapper.findCourses(professorUserId, semester, keyword).stream()
            .map(this::toCourseItem)
            .toList();
    ProfessorCourseStatistics statistics =
        courseMapper.findStatistics(professorUserId, semester);

    return new ProfessorCourseListResponse(
        courses,
        new Statistics(
            statistics.getTotalCourses(),
            statistics.getTotalStudents(),
            satisfactionValue(statistics.getAvgSatisfaction())));
  }

  private CourseItem toCourseItem(ProfessorCourseItem course) {
    return new CourseItem(
        course.getCourseId(),
        course.getCourseName(),
        course.getDivision(),
        course.getCredit(),
        course.getSchedule(),
        course.getRoom(),
        course.getCapacity(),
        course.getEnrolled(),
        satisfactionValue(course.getAvgSatisfaction()));
  }

  private Object satisfactionValue(Double value) {
    return value == null ? "-" : value;
  }
}
