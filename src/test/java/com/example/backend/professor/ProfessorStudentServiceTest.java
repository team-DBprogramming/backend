package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorStudentItem;
import com.example.backend.dto.professor.ProfessorStudentListResponse;
import com.example.backend.dto.professor.ProfessorStudentSummary;
import com.example.backend.mapper.ProfessorStudentMapper;
import com.example.backend.service.ProfessorStudentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorStudentServiceTest {

  private FakeProfessorStudentMapper studentMapper;
  private ProfessorStudentService studentService;

  @BeforeEach
  void setUp() {
    studentMapper = new FakeProfessorStudentMapper();
    studentService = new ProfessorStudentService(studentMapper);
  }

  @Test
  void getStudentsUsesCallableProcedureResultCursors() {
    studentMapper.result = "SUCCESS";
    studentMapper.summary =
        new ProfessorStudentSummary("Database", "CSE301", "01분반", "2026-1학기", 6, 3);
    studentMapper.students.add(
        new ProfessorStudentItem("2024000001", "Student One", 3, "Computer Science", true));
    studentMapper.students.add(
        new ProfessorStudentItem("2024000002", "Student Two", 3, "Computer Science", false));

    ProfessorStudentListResponse response =
        studentService.getStudents(
            professorUser(), "CSE301", "01", " 2024 ", 3, " Computer ", 1, 20);

    assertThat(response.summary().courseId()).isEqualTo("CSE301");
    assertThat(response.summary().division()).isEqualTo("01분반");
    assertThat(response.students()).hasSize(2);
    assertThat(response.students().get(0).isRetake()).isTrue();
    assertThat(studentMapper.params).containsEntry("professorUserId", 10L);
    assertThat(studentMapper.params).containsEntry("courseId", "CSE301");
    assertThat(studentMapper.params).containsEntry("division", "01");
    assertThat(studentMapper.params).containsEntry("keyword", " 2024 ");
    assertThat(studentMapper.params).containsEntry("grade", 3);
    assertThat(studentMapper.params).containsEntry("major", " Computer ");
    assertThat(studentMapper.params).containsEntry("page", 1);
    assertThat(studentMapper.params).containsEntry("size", 20);
  }

  @Test
  void getStudentsMapsProcedureDivisionError() {
    studentMapper.result = "DIVISION_REQUIRED";

    assertThatThrownBy(
            () -> studentService.getStudents(professorUser(), "CSE301", "", null, null, null, 1, 20))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode())
                    .isEqualTo("PROFESSOR4005"));
  }

  @Test
  void getStudentsMapsProcedureNotFoundError() {
    studentMapper.result = "NOT_FOUND";

    assertThatThrownBy(
            () ->
                studentService.getStudents(
                    professorUser(), "UNKNOWN", "01", null, null, null, 1, 20))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode())
                    .isEqualTo("PROFESSOR4041"));
  }

  private static class FakeProfessorStudentMapper implements ProfessorStudentMapper {
    private String result;
    private ProfessorStudentSummary summary;
    private final List<ProfessorStudentItem> students = new ArrayList<>();
    private Map<String, Object> params;

    @Override
    public void callGetProfessorStudentList(Map<String, Object> params) {
      this.params = params;
      params.put("result", result);
      if (summary != null) {
        params.put("summary", List.of(summary));
      }
      params.put("students", students);
    }
  }
}
