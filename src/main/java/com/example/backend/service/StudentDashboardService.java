package com.example.backend.service;

import com.example.backend.dto.student.StudentCreditSummary;
import com.example.backend.dto.student.StudentDashboardResponse;
import com.example.backend.dto.student.StudentEnrollmentStatus;
import com.example.backend.dto.student.StudentInfo;
import com.example.backend.dto.student.StudentQuickActions;
import com.example.backend.dto.student.StudentTodaySchedule;
import com.example.backend.mapper.StudentDashboardMapper;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.utils.SemesterUtils;
import com.example.backend.utils.SemesterUtils.Semester;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentDashboardService {

  private static final int MAX_CREDITS = 18;
  private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

  private static final DateTimeFormatter DEADLINE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final StudentDashboardMapper dashboardMapper;
  private final Clock clock;

  public StudentDashboardService(
      StudentDashboardMapper dashboardMapper,
      Clock clock
  ) {
    this.dashboardMapper = dashboardMapper;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public StudentDashboardResponse getDashboard(
      AuthenticatedUser currentUser,
      String semester
  ) {
    String studentId = currentUser.requireStudentId();

    ZonedDateTime now =
        ZonedDateTime.now(clock.withZone(SEOUL_ZONE));

    Semester normalizedSemester =
        SemesterUtils.current(LocalDate.from(now));

    String semesterLabel =
        SemesterUtils.format(normalizedSemester);

    String today =
        toSchemaDayOfWeek(now.getDayOfWeek());

    StudentInfo studentInfo =
        dashboardMapper.findStudentInfo(
            studentId,
            semesterLabel
        );

    // GET_ENROLL_STATUS 프로시저 호출
    StudentEnrollmentStatus enrollmentStatus =
        getEnrollmentStatus();

    StudentCreditSummary creditSummary =
        nullToEmptyCreditSummary(getCreditSummary(studentId));

    List<StudentTodaySchedule> todaySchedule =
        markNextSchedule(
            dashboardMapper.findTodaySchedules(
                studentId,
                today,
                normalizedSemester.year(),
                normalizedSemester.semester()
            ),
            now.toLocalTime()
        );

    StudentQuickActions quickActions =
        nullToEmptyQuickActions(
            dashboardMapper.findQuickActions(
                studentId,
                normalizedSemester.year(),
                normalizedSemester.semester()
            )
        );

    return new StudentDashboardResponse(
        studentInfo,
        enrollmentStatus,
        creditSummary,
        todaySchedule,
        quickActions
    );
  }

  /**
   * Oracle GET_ENROLL_STATUS 프로시저의 OUT 파라미터를 받아
   * 대시보드용 StudentEnrollmentStatus DTO로 변환한다.
   */
  private StudentEnrollmentStatus getEnrollmentStatus() {
    Map<String, Object> params = new HashMap<>();

    dashboardMapper.callGetEnrollStatus(params);

    String status =
        (String) params.get("status");

    Timestamp deadlineTimestamp =
        (Timestamp) params.get("deadline");

    Number daysLeftValue =
        (Number) params.get("daysLeft");

    String deadline =
        deadlineTimestamp == null
            ? null
            : deadlineTimestamp
                .toLocalDateTime()
                .format(DEADLINE_FORMATTER);

    int daysLeft =
        daysLeftValue == null
            ? 0
            : daysLeftValue.intValue();

    return new StudentEnrollmentStatus(
        status == null ? "CLOSED" : status,
        deadline,
        daysLeft
    );
  }

  private StudentCreditSummary getCreditSummary(String studentId) {
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);

    dashboardMapper.callGetCreditSummary(params);

    return new StudentCreditSummary(
        numberValue(params.get("applied")),
        numberValue(params.get("max")),
        numberValue(params.get("courseCount")),
        numberValue(params.get("cartCount"))
    );
  }

  private Integer numberValue(Object value) {
    if (value == null) {
      return 0;
    }
    if (value instanceof Number number) {
      return number.intValue();
    }
    return Integer.parseInt(String.valueOf(value));
  }

  private List<StudentTodaySchedule> markNextSchedule(
      List<StudentTodaySchedule> schedules,
      LocalTime now
  ) {
    StudentTodaySchedule next = null;

    for (StudentTodaySchedule schedule : schedules) {
      if (!isBlank(schedule.getStartTime())
          && LocalTime.parse(schedule.getStartTime()).isAfter(now)) {
        next = schedule;
        break;
      }
    }

    for (StudentTodaySchedule schedule : schedules) {
      schedule.setNext(schedule == next);
    }

    return schedules;
  }

  private StudentCreditSummary nullToEmptyCreditSummary(
      StudentCreditSummary summary
  ) {
    return summary == null
        ? new StudentCreditSummary(0, MAX_CREDITS, 0, 0)
        : summary;
  }

  private StudentQuickActions nullToEmptyQuickActions(
      StudentQuickActions quickActions
  ) {
    return quickActions == null
        ? new StudentQuickActions(0, 0)
        : quickActions;
  }

  private String toSchemaDayOfWeek(DayOfWeek dayOfWeek) {
    return switch (dayOfWeek) {
      case MONDAY -> "MON";
      case TUESDAY -> "TUE";
      case WEDNESDAY -> "WED";
      case THURSDAY -> "THU";
      case FRIDAY -> "FRI";
      case SATURDAY -> "SAT";
      case SUNDAY -> "SUN";
    };
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
