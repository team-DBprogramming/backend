package com.example.backend.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static com.example.backend.support.TestAuthentications.professorUser;
import static com.example.backend.support.TestAuthentications.studentUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorStudentExportCourse;
import com.example.backend.dto.professor.ProfessorStudentExportFile;
import com.example.backend.dto.professor.ProfessorStudentExportRow;
import com.example.backend.mapper.ProfessorStudentExportMapper;
import com.example.backend.service.ProfessorStudentExportService;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
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
  void exportXlsxUsesFiltersAndFeatureSpecFileName() {
    exportMapper.course =
        new ProfessorStudentExportCourse("데이터베이스개론", "CSE301", "01분반", "2026-1학기");
    exportMapper.rows.add(
        new ProfessorStudentExportRow(
            "2024111111", "홍길동", 3, "컴퓨터공학과", "ENROLLED", "2026-02-09 09:12", null));

    ProfessorStudentExportFile file =
        exportService.exportStudents(professorUser(), "CSE301", "xlsx", "홍", 3, "컴퓨터");

    assertThat(exportMapper.requestedProfessorUserId).isEqualTo(10L);
    assertThat(exportMapper.requestedCourseId).isEqualTo("CSE301");
    assertThat(exportMapper.requestedKeyword).isEqualTo("홍");
    assertThat(exportMapper.requestedGrade).isEqualTo(3);
    assertThat(exportMapper.requestedMajor).isEqualTo("컴퓨터");
    assertThat(file.filename()).isEqualTo("학생관리_데이터베이스개론_2026-1학기_2026-05-31.xlsx");
    assertThat(file.contentType())
        .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    assertThat(file.bytes()).startsWith(new byte[] {'P', 'K'});
  }

  @Test
  void exportCsvUsesCsvContentTypeAndExtension() {
    exportMapper.course =
        new ProfessorStudentExportCourse("데이터베이스개론", "CSE301", "01분반", "2026-1학기");
    exportMapper.rows.add(
        new ProfessorStudentExportRow(
            "2024111111", "홍길동", 3, "컴퓨터공학과", "ENROLLED", "2026-02-09 09:12", null));

    ProfessorStudentExportFile file =
        exportService.exportStudents(professorUser(), "CSE301", "csv", null, null, null);

    assertThat(file.filename()).endsWith(".csv");
    assertThat(file.contentType()).isEqualTo("text/csv;charset=UTF-8");
    assertThat(new String(file.bytes())).contains("studentId,name,grade,major,status,enrolledAt,note");
  }

  @Test
  void exportRejectsUnsupportedFormat() {

    assertThatThrownBy(
            () -> exportService.exportStudents(professorUser(), "CSE301", "pdf", null, null, null))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4003"));
  }

  private static class FakeProfessorStudentExportMapper implements ProfessorStudentExportMapper {
    private Long requestedProfessorUserId;
    private String requestedCourseId;
    private String requestedKeyword;
    private Integer requestedGrade;
    private String requestedMajor;
    private ProfessorStudentExportCourse course;
    private final List<ProfessorStudentExportRow> rows = new ArrayList<>();

    @Override
    public ProfessorStudentExportCourse findCourse(Long professorUserId, String courseId) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      return course;
    }

    @Override
    public List<ProfessorStudentExportRow> findStudents(
        Long professorUserId, String courseId, String keyword, Integer grade, String major) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      requestedKeyword = keyword;
      requestedGrade = grade;
      requestedMajor = major;
      return rows;
    }
  }
}

