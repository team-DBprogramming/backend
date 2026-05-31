package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorStudentItem;
import com.example.backend.dto.professor.ProfessorStudentListResponse;
import com.example.backend.dto.professor.ProfessorStudentSummary;
import com.example.backend.mapper.ProfessorStudentMapper;
import com.example.backend.utils.JwtTokenProvider;
import com.example.backend.utils.TokenClaims;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorStudentService {

  private final ProfessorStudentMapper studentMapper;
  private final JwtTokenProvider tokenProvider;

  public ProfessorStudentService(ProfessorStudentMapper studentMapper, JwtTokenProvider tokenProvider) {
    this.studentMapper = studentMapper;
    this.tokenProvider = tokenProvider;
  }

  @Transactional(readOnly = true)
  public ProfessorStudentListResponse getStudents(
      String authorizationHeader,
      String courseId,
      String division,
      String keyword,
      Integer grade,
      String major,
      Integer page,
      Integer size) {
    TokenClaims claims = validateProfessor(authorizationHeader);
    String normalizedDivision = normalizeRequiredDivision(division);
    int pageSize = positiveOrDefault(size, 20);
    int offset = (positiveOrDefault(page, 1) - 1) * pageSize;

    ProfessorStudentSummary summary =
        studentMapper.findStudentSummary(claims.userId(), courseId, normalizedDivision);
    if (summary == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    List<ProfessorStudentItem> students =
        studentMapper.findStudents(
            claims.userId(),
            courseId,
            normalizedDivision,
            normalize(keyword),
            grade,
            normalize(major),
            pageSize,
            offset);
    return new ProfessorStudentListResponse(summary, students);
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
