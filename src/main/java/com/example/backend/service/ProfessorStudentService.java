package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorStudentItem;
import com.example.backend.dto.professor.ProfessorStudentListResponse;
import com.example.backend.dto.professor.ProfessorStudentSummary;
import com.example.backend.mapper.ProfessorStudentMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorStudentService {

  private static final String SUCCESS = "SUCCESS";
  private static final String NOT_FOUND = "NOT_FOUND";
  private static final String DIVISION_REQUIRED = "DIVISION_REQUIRED";

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
    Map<String, Object> params = new HashMap<>();
    params.put("professorUserId", currentUser.requireProfessorUserId());
    params.put("courseId", courseId);
    params.put("division", division);
    params.put("keyword", keyword);
    params.put("grade", grade);
    params.put("major", major);
    params.put("page", page);
    params.put("size", size);

    studentMapper.callGetProfessorStudentList(params);
    handleResult(stringValue(params.get("result")));

    ProfessorStudentSummary summary = firstSummary(params.get("summary"));
    if (summary == null) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    return new ProfessorStudentListResponse(summary, studentItems(params.get("students")));
  }

  private void handleResult(String result) {
    if (SUCCESS.equals(result)) {
      return;
    }
    if (DIVISION_REQUIRED.equals(result)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_DIVISION_REQUIRED);
    }
    if (NOT_FOUND.equals(result)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
  }

  private String stringValue(Object value) {
    return value == null ? null : String.valueOf(value);
  }

  private ProfessorStudentSummary firstSummary(Object value) {
    if (value instanceof List<?> rows && !rows.isEmpty()) {
      return (ProfessorStudentSummary) rows.get(0);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private List<ProfessorStudentItem> studentItems(Object value) {
    return value instanceof List<?> rows ? (List<ProfessorStudentItem>) rows : List.of();
  }
}
