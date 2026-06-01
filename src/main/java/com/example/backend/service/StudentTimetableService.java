package com.example.backend.service;

import com.example.backend.dto.student.StudentAiTimetableRequest;
import com.example.backend.dto.student.StudentTimetableResponse;
import com.example.backend.mapper.StudentTimetableMapper;
import com.example.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentTimetableService {

  private static final int DEFAULT_RECOMMEND_CREDITS = 18;

  private final StudentTimetableMapper timetableMapper;

  public StudentTimetableService(StudentTimetableMapper timetableMapper) {
    this.timetableMapper = timetableMapper;
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse getTimetable(AuthenticatedUser currentUser, String semester) {
    Long userId = currentUser.requireStudentUserId();
    return new StudentTimetableResponse(timetableMapper.findEnrollmentTimetable(userId, normalize(semester)));
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse exportCart(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    return new StudentTimetableResponse(timetableMapper.findCartTimetable(userId));
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse recommend(AuthenticatedUser currentUser, StudentAiTimetableRequest request) {
    Long userId = currentUser.requireStudentUserId();
    Integer maxCredits =
        request == null || request.maxCredits() == null ? DEFAULT_RECOMMEND_CREDITS : request.maxCredits();
    String keyword = request == null ? null : normalize(request.keyword());
    return new StudentTimetableResponse(timetableMapper.findRecommendedTimetable(userId, maxCredits, keyword));
  }

  private String normalize(String value) {
    return value == null || value.trim().isEmpty() ? null : value.trim();
  }
}
