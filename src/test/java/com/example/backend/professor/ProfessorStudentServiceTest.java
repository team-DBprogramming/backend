package com.example.backend.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static com.example.backend.support.TestAuthentications.professorUser;
import static com.example.backend.support.TestAuthentications.studentUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorStudentItem;
import com.example.backend.dto.professor.ProfessorStudentListResponse;
import com.example.backend.dto.professor.ProfessorStudentSummary;
import com.example.backend.mapper.ProfessorStudentMapper;
import com.example.backend.service.ProfessorStudentService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorStudentServiceTest {

  private final Clock clock = Clock.fixed(Instant.parse("2026-05-31T00:00:00Z"), ZoneOffset.UTC);
  private FakeProfessorStudentMapper studentMapper;
  private ProfessorStudentService studentService;

  @BeforeEach
  void setUp() {
    studentMapper = new FakeProfessorStudentMapper();
    studentService = new ProfessorStudentService(studentMapper);
  }

  @Test
  void getStudentsReturnsSelectedDivisionSummaryAndPagedStudents() {
    studentMapper.summary =
        new ProfessorStudentSummary("데이터베이스개론", "CSE301", "01분반", "2026-1", 6, 3);
    studentMapper.students.add(
        new ProfessorStudentItem("2024000001", "정도훈", 3, "컴퓨터공학과", true));
    studentMapper.students.add(
        new ProfessorStudentItem("2024000002", "김민수", 3, "컴퓨터공학과", false));

    ProfessorStudentListResponse response =
        studentService.getStudents(
            professorUser(), "CSE301", "01", "2024", 3, "컴퓨터", 1, 20);

    assertThat(response.summary().courseId()).isEqualTo("CSE301");
    assertThat(response.summary().division()).isEqualTo("01분반");
    assertThat(response.students()).hasSize(2);
    assertThat(response.students().get(0).isRetake()).isTrue();
    assertThat(studentMapper.requestedProfessorUserId).isEqualTo(10L);
    assertThat(studentMapper.requestedCourseId).isEqualTo("CSE301");
    assertThat(studentMapper.requestedDivision).isEqualTo("01");
    assertThat(studentMapper.requestedKeyword).isEqualTo("2024");
    assertThat(studentMapper.requestedGrade).isEqualTo(3);
    assertThat(studentMapper.requestedMajor).isEqualTo("컴퓨터");
    assertThat(studentMapper.requestedSize).isEqualTo(20);
    assertThat(studentMapper.requestedOffset).isZero();
  }

  @Test
  void getStudentsRejectsMissingDivision() {

    assertThatThrownBy(
            () -> studentService.getStudents(professorUser(), "CSE301", "", null, null, null, 1, 20))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4005"));
  }

  private static class FakeProfessorStudentMapper implements ProfessorStudentMapper {
    private ProfessorStudentSummary summary;
    private final List<ProfessorStudentItem> students = new ArrayList<>();
    private Long requestedProfessorUserId;
    private String requestedCourseId;
    private String requestedDivision;
    private String requestedKeyword;
    private Integer requestedGrade;
    private String requestedMajor;
    private int requestedSize;
    private int requestedOffset;

    @Override
    public ProfessorStudentSummary findStudentSummary(
        Long professorUserId, String courseId, String division) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      requestedDivision = division;
      return summary;
    }

    @Override
    public List<ProfessorStudentItem> findStudents(
        Long professorUserId,
        String courseId,
        String division,
        String keyword,
        Integer grade,
        String major,
        int size,
        int offset) {
      requestedProfessorUserId = professorUserId;
      requestedCourseId = courseId;
      requestedDivision = division;
      requestedKeyword = keyword;
      requestedGrade = grade;
      requestedMajor = major;
      requestedSize = size;
      requestedOffset = offset;
      return students;
    }
  }
}

