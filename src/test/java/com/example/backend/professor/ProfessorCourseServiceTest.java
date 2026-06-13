package com.example.backend.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static com.example.backend.support.TestAuthentications.professorUser;
import static com.example.backend.support.TestAuthentications.studentUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorCourseItem;
import com.example.backend.dto.professor.ProfessorCourseListResponse;
import com.example.backend.dto.professor.ProfessorCourseStatistics;
import com.example.backend.mapper.ProfessorCourseMapper;
import com.example.backend.service.ProfessorCourseService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorCourseServiceTest {

  private final Clock clock = Clock.fixed(Instant.parse("2026-05-31T00:00:00Z"), ZoneOffset.UTC);
  private FakeProfessorCourseMapper courseMapper;
  private ProfessorCourseService courseService;

  @BeforeEach
  void setUp() {
    courseMapper = new FakeProfessorCourseMapper();
    courseService = new ProfessorCourseService(courseMapper);
  }

  @Test
  void getCoursesReturnsFilteredCoursesAndUnfilteredStatistics() {
    courseMapper.statistics = new ProfessorCourseStatistics(4, 91, 4.5);
    courseMapper.courses.add(
        new ProfessorCourseItem(
            "CSE301",
            "데이터베이스개론",
            "01분반",
            3,
            "월수 10:30-12:00",
            "A동 301호",
            35,
            28,
            4.5));

    ProfessorCourseListResponse response =
        courseService.getCourses(professorUser(), "2026-1", "데이터");

    assertThat(courseMapper.requestedUserId).isEqualTo(10L);
    assertThat(courseMapper.requestedSemester).isEqualTo("2026-1");
    assertThat(courseMapper.requestedKeyword).isEqualTo("데이터");
    assertThat(response.courses()).hasSize(1);
    assertThat(response.courses().get(0).courseId()).isEqualTo("CSE301");
    assertThat(response.courses().get(0).avgSatisfaction()).isEqualTo(4.5);
    assertThat(response.statistics().totalCourses()).isEqualTo(4);
    assertThat(response.statistics().totalStudents()).isEqualTo(91);
    assertThat(response.statistics().avgSatisfaction()).isEqualTo(4.5);
  }

  @Test
  void getCoursesUsesDashWhenSatisfactionIsMissing() {
    courseMapper.statistics = new ProfessorCourseStatistics(1, 0, null);
    courseMapper.courses.add(
        new ProfessorCourseItem("CSE301", "데이터베이스개론", "01분반", 3, "", "", 35, 0, null));

    ProfessorCourseListResponse response = courseService.getCourses(professorUser(), null, null);

    assertThat(response.courses().get(0).avgSatisfaction()).isEqualTo("-");
    assertThat(response.statistics().avgSatisfaction()).isEqualTo("-");
  }

  @Test
  void getCoursesLeavesParameterNormalizationToSql() {
    courseMapper.statistics = new ProfessorCourseStatistics(0, 0, null);

    courseService.getCourses(professorUser(), " 2026-1 ", " database ");

    assertThat(courseMapper.requestedSemester).isEqualTo(" 2026-1 ");
    assertThat(courseMapper.requestedKeyword).isEqualTo(" database ");
  }

  @Test
  void getCoursesRejectsStudentToken() {

    assertThatThrownBy(() -> courseService.getCourses(studentUser(), "2026-1", null))
        .isInstanceOfSatisfying(
            ProfessorHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("PROFESSOR4031"));
  }

  private static class FakeProfessorCourseMapper implements ProfessorCourseMapper {
    private Long requestedUserId;
    private String requestedSemester;
    private String requestedKeyword;
    private ProfessorCourseStatistics statistics;
    private final List<ProfessorCourseItem> courses = new ArrayList<>();

    @Override
    public List<ProfessorCourseItem> findCourses(Long userId, String semester, String keyword) {
      requestedUserId = userId;
      requestedSemester = semester;
      requestedKeyword = keyword;
      return courses;
    }

    @Override
    public ProfessorCourseStatistics findStatistics(Long userId, String semester) {
      requestedUserId = userId;
      requestedSemester = semester;
      return statistics;
    }
  }
}

