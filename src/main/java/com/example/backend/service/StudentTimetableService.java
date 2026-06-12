package com.example.backend.service;

import com.example.backend.dto.student.StudentLectureTime;
import com.example.backend.dto.student.StudentTimetableItem;
import com.example.backend.dto.student.StudentTimetableResponse;
import com.example.backend.mapper.StudentTimetableMapper;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.utils.SemesterUtils;
import com.example.backend.utils.SemesterUtils.Semester;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentTimetableService {

  private final StudentTimetableMapper timetableMapper;
  private final Clock clock;

  public StudentTimetableService(StudentTimetableMapper timetableMapper, Clock clock) {
    this.timetableMapper = timetableMapper;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse getTimetable(AuthenticatedUser currentUser, String semester) {
    String studentId = currentUser.requireStudentId();
    Semester currentSemester = SemesterUtils.current(LocalDate.now(clock));
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    timetableMapper.callGetEnrollmentTimetable(params);
    return toResponse(
        SemesterUtils.format(currentSemester),
        listValue(params.get("rows")),
        true);
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse exportCart(AuthenticatedUser currentUser) {
    String studentId = currentUser.requireStudentId();
    return toResponse(null, timetableMapper.findCartTimetable(studentId), true);
  }

  @SuppressWarnings("unchecked")
  private static List<StudentTimetableItem> listValue(Object value) {
    return value instanceof List<?> list ? (List<StudentTimetableItem>) list : List.of();
  }

  public static StudentTimetableResponse toResponse(
      String semester, List<StudentTimetableItem> rows, boolean includeStatistics) {
    Map<String, CourseBuilder> grouped = new LinkedHashMap<>();
    for (StudentTimetableItem row : rows) {
      grouped
          .computeIfAbsent(
              row.getCourseId(),
              key ->
                  new CourseBuilder(
                      row.getCourseId(),
                      row.getCourseName(),
                      row.getCourseType(),
                      row.getRoom(),
                      row.getProfessor(),
                      row.getCredit()))
          .lectureTimes()
          .add(new StudentLectureTime(row.getDayOfWeek(), row.getStartTime(), row.getEndTime()));
    }

    List<StudentTimetableResponse.Course> courses =
        grouped.values().stream()
            .map(
                course ->
                    new StudentTimetableResponse.Course(
                        course.courseId(),
                        course.courseName(),
                        course.courseType(),
                        course.lectureTimes(),
                        course.room(),
                        course.professor()))
            .toList();
    int totalCredit =
        grouped.values().stream()
            .map(CourseBuilder::credit)
            .filter(credit -> credit != null)
            .mapToInt(Integer::intValue)
            .sum();
    StudentTimetableResponse.Statistics statistics =
        includeStatistics ? toStatistics(grouped.values().stream().toList()) : null;
    return new StudentTimetableResponse(semester, courses, totalCredit, statistics);
  }

  private static StudentTimetableResponse.Statistics toStatistics(List<CourseBuilder> courses) {
    long totalMinutes = 0;
    Set<String> allDays = Set.of("MON", "TUE", "WED", "THU", "FRI");
    List<String> occupiedDays = new ArrayList<>();
    for (CourseBuilder course : courses) {
      for (StudentLectureTime time : course.lectureTimes()) {
        occupiedDays.add(time.dayOfWeek());
        if (time.startTime() != null && time.endTime() != null) {
          totalMinutes += Duration.between(LocalTime.parse(time.startTime()), LocalTime.parse(time.endTime())).toMinutes();
        }
      }
    }
    List<String> freeDays = allDays.stream().filter(day -> !occupiedDays.contains(day)).toList();
    int activeDays = Math.max(1, (int) occupiedDays.stream().distinct().count());
    long averageMinutes = totalMinutes / activeDays;
    String averageDailyHours = String.format("%02d:%02d", averageMinutes / 60, averageMinutes % 60);
    return new StudentTimetableResponse.Statistics(
        (int) Math.round(totalMinutes / 60.0), averageDailyHours, freeDays, courses.size());
  }

  private record CourseBuilder(
      String courseId,
      String courseName,
      String courseType,
      String room,
      String professor,
      Integer credit,
      List<StudentLectureTime> lectureTimes) {

    private CourseBuilder(
        String courseId,
        String courseName,
        String courseType,
        String room,
        String professor,
        Integer credit) {
      this(courseId, courseName, courseType, room, professor, credit, new ArrayList<>());
    }
  }
}
