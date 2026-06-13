package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorStudentExportCourse;
import com.example.backend.dto.professor.ProfessorStudentExportFile;
import com.example.backend.dto.professor.ProfessorStudentExportRow;
import com.example.backend.mapper.ProfessorStudentExportMapper;
import com.example.backend.service.ProfessorStudentExportService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorStudentExportServiceTest {

  private final Clock clock =
      Clock.fixed(Instant.parse("2026-05-31T00:00:00Z"), ZoneId.of("Asia/Seoul"));
  private FakeProfessorStudentExportMapper exportMapper;
  private ProfessorStudentExportService exportService;

  @BeforeEach
  void setUp() {
    exportMapper = new FakeProfessorStudentExportMapper();
    exportService = new ProfessorStudentExportService(exportMapper, clock);
  }

  @Test
  void exportXlsxUsesCallableProceduresAndFeatureSpecFileName() {
    exportMapper.courseRows.add(
        new ProfessorStudentExportCourse("Database", "CSE301", "01분반", "2026-1학기"));
    exportMapper.studentRows.add(
        new ProfessorStudentExportRow(
            "2024111111", "Student One", 3, "Computer Science", "ENROLLED", "2026-02-09 09:12", null));

    ProfessorStudentExportFile file =
        exportService.exportStudents(
            professorUser(), "CSE301", "01", "xlsx", "student", 3, "computer");

    assertThat(exportMapper.courseParams)
        .containsEntry("professorUserId", 10L)
        .containsEntry("courseId", "CSE301")
        .containsEntry("division", "01");
    assertThat(exportMapper.studentParams)
        .containsEntry("professorUserId", 10L)
        .containsEntry("courseId", "CSE301")
        .containsEntry("division", "01")
        .containsEntry("keyword", "student")
        .containsEntry("grade", 3)
        .containsEntry("major", "computer");
    assertThat(file.filename()).isEqualTo("학생관리_Database_2026-1학기_2026-05-31.xlsx");
    assertThat(file.contentType())
        .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    assertThat(file.bytes()).startsWith(new byte[] {'P', 'K'});
  }

  @Test
  void exportRejectsCsvAfterCsvSupportIsRemoved() {
    assertThatThrownBy(
            () -> exportService.exportStudents(professorUser(), "CSE301", "01", "csv", null, null, null))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4003"));
  }

  @Test
  void exportRejectsMissingCourseFromProcedureResult() {
    exportMapper.courseResult = "COURSE_NOT_FOUND";

    assertThatThrownBy(
            () -> exportService.exportStudents(professorUser(), "CSE404", "01", "xlsx", null, null, null))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4041"));
  }

  private static class FakeProfessorStudentExportMapper implements ProfessorStudentExportMapper {
    private String courseResult = "SUCCESS";
    private Map<String, Object> courseParams;
    private Map<String, Object> studentParams;
    private final List<ProfessorStudentExportCourse> courseRows = new ArrayList<>();
    private final List<ProfessorStudentExportRow> studentRows = new ArrayList<>();

    @Override
    public void callGetExportCourse(Map<String, Object> params) {
      courseParams = params;
      params.put("result", courseResult);
      params.put("course", courseRows);
    }

    @Override
    public void callGetExportStudents(Map<String, Object> params) {
      studentParams = params;
      params.put("rows", studentRows);
    }
  }
}
