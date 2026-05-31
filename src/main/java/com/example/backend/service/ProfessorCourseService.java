package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorCourseItem;
import com.example.backend.dto.professor.ProfessorCourseListResponse;
import com.example.backend.dto.professor.ProfessorCourseListResponse.CourseItem;
import com.example.backend.dto.professor.ProfessorCourseListResponse.Statistics;
import com.example.backend.dto.professor.ProfessorCourseStatistics;
import com.example.backend.mapper.ProfessorCourseMapper;
import com.example.backend.utils.JwtTokenProvider;
import com.example.backend.utils.TokenClaims;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorCourseService {

  private final ProfessorCourseMapper courseMapper;
  private final JwtTokenProvider tokenProvider;

  public ProfessorCourseService(ProfessorCourseMapper courseMapper, JwtTokenProvider tokenProvider) {
    this.courseMapper = courseMapper;
    this.tokenProvider = tokenProvider;
  }

  @Transactional(readOnly = true)
  public ProfessorCourseListResponse getCourses(
      String authorizationHeader, String semester, String keyword) {
    TokenClaims claims = validateProfessor(authorizationHeader);
    String normalizedSemester = normalize(semester);
    String normalizedKeyword = normalize(keyword);
    List<CourseItem> courses =
        courseMapper.findCourses(claims.userId(), normalizedSemester, normalizedKeyword).stream()
            .map(this::toCourseItem)
            .toList();
    ProfessorCourseStatistics statistics =
        nullToEmptyStatistics(courseMapper.findStatistics(claims.userId(), normalizedSemester));

    return new ProfessorCourseListResponse(
        courses,
        new Statistics(
            intValue(statistics.getTotalCourses()),
            intValue(statistics.getTotalStudents()),
            satisfactionValue(statistics.getAvgSatisfaction())));
  }

  private TokenClaims validateProfessor(String authorizationHeader) {
    if (isBlank(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
    TokenClaims claims =
        tokenProvider.validateAccessToken(authorizationHeader.substring("Bearer ".length()).trim());
    if (!"PROFESSOR".equals(claims.role())) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_FORBIDDEN);
    }
    return claims;
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
