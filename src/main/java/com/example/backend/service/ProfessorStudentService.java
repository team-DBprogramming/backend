package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorStudentItem;
import com.example.backend.dto.professor.ProfessorStudentListResponse;
import com.example.backend.dto.professor.ProfessorStudentSummary;
import com.example.backend.mapper.ProfessorStudentMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorStudentService {

  private final ProfessorStudentMapper studentMapper;

  public ProfessorStudentService(ProfessorStudentMapper studentMapper) {
    this.studentMapper = studentMapper;
  }

  @Transactional(readOnly = true)
  public ProfessorStudentListResponse getStudents(
      AuthenticatedUser currentUser,
      String courseId,
      String division,
      String keyword,
      Integer grade,
      String major,
      Integer page,
      Integer size) {
    Long professorUserId = currentUser.requireProfessorUserId();
    String normalizedDivision = normalizeRequiredDivision(division);
    int pageSize = positiveOrDefault(size, 20);
    int offset = (positiveOrDefault(page, 1) - 1) * pageSize;

    ProfessorStudentSummary summary =
        studentMapper.findStudentSummary(professorUserId, courseId, normalizedDivision);
    if (summary == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    List<ProfessorStudentItem> students =
        studentMapper.findStudents(
            professorUserId,
            courseId,
            normalizedDivision,
            normalize(keyword),
            grade,
            normalize(major),
            pageSize,
            offset);
    return new ProfessorStudentListResponse(summary, students);
  }

  private String normalizeRequiredDivision(String division) {
    if (isBlank(division)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_DIVISION_REQUIRED);
    }
    return division.trim();
  }

  private String normalize(String value) {
    return isBlank(value) ? null : value.trim().toLowerCase();
  }

  private int positiveOrDefault(Integer value, int defaultValue) {
    return value == null || value <= 0 ? defaultValue : value;
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
