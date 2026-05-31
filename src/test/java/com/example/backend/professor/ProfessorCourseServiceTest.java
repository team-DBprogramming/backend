package com.example.backend.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorCourseItem;
import com.example.backend.dto.professor.ProfessorCourseListResponse;
import com.example.backend.dto.professor.ProfessorCourseStatistics;
import com.example.backend.mapper.ProfessorCourseMapper;
import com.example.backend.service.ProfessorCourseService;
import com.example.backend.utils.JwtTokenProvider;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorCourseServiceTest {

  private final Clock clock = Clock.fixed(Instant.parse("2026-05-31T00:00:00Z"), ZoneOffset.UTC);
  private FakeProfessorCourseMapper courseMapper;
  private JwtTokenProvider tokenProvider;
  private ProfessorCourseService courseService;

  @BeforeEach
  void setUp() {
    courseMapper = new FakeProfessorCourseMapper();
    tokenProvider =
        new JwtTokenProvider(
            "test-secret-key-test-secret-key-test-secret-key",
            Duration.ofMinutes(30),
            Duration.ofDays(1),
            Duration.ofDays(30),
            clock);
    courseService = new ProfessorCourseService(courseMapper, tokenProvider);
  }

  @Test
  void getCoursesReturnsFilteredCoursesAndUnfilteredStatistics() {
    String accessToken = tokenProvider.createAccessToken(10L, "P1001", "PROFESSOR").token();
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
        courseService.getCourses("Bearer " + accessToken, "2026-1", "데이터");

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
    String accessToken = tokenProvider.createAccessToken(10L, "P1001", "PROFESSOR").token();
    courseMapper.statistics = new ProfessorCourseStatistics(1, 0, null);
    courseMapper.courses.add(
        new ProfessorCourseItem("CSE301", "데이터베이스개론", "01분반", 3, "", "", 35, 0, null));

    ProfessorCourseListResponse response = courseService.getCourses("Bearer " + accessToken, null, null);

    assertThat(response.courses().get(0).avgSatisfaction()).isEqualTo("-");
    assertThat(response.statistics().avgSatisfaction()).isEqualTo("-");
  }

  @Test
  void getCoursesRejectsStudentToken() {
    String accessToken = tokenProvider.createAccessToken(10L, "2024123456", "STUDENT").token();

    assertThatThrownBy(() -> courseService.getCourses("Bearer " + accessToken, "2026-1", null))
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
