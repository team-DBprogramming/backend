package com.example.backend.service;

import com.example.backend.dto.student.StudentAiTimetableRequest;
import com.example.backend.dto.student.StudentLectureTime;
import com.example.backend.dto.student.StudentTimetableItem;
import com.example.backend.dto.student.StudentTimetableResponse;
import com.example.backend.mapper.StudentTimetableMapper;
import com.example.backend.security.AuthenticatedUser;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentTimetableService {

  private static final int DEFAULT_RECOMMEND_CREDITS = 18;

  private final StudentTimetableMapper timetableMapper;

  public StudentTimetableService(StudentTimetableMapper timetableMapper) {
    this.timetableMapper = timetableMapper;
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse getTimetable(AuthenticatedUser currentUser, String semester) {
    Long userId = currentUser.requireStudentUserId();
    String normalizedSemester = normalize(semester);
    return toResponse(normalizedSemester, timetableMapper.findEnrollmentTimetable(userId, normalizedSemester), true);
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse exportCart(AuthenticatedUser currentUser) {
    Long userId = currentUser.requireStudentUserId();
    return toResponse(null, timetableMapper.findCartTimetable(userId), true);
  }

  @Transactional(readOnly = true)
  public StudentTimetableResponse recommend(AuthenticatedUser currentUser, StudentAiTimetableRequest request) {
    Long userId = currentUser.requireStudentUserId();
    Integer maxCredits =
        request == null || request.maxCredits() == null ? DEFAULT_RECOMMEND_CREDITS : request.maxCredits();
    String keyword = request == null ? null : normalize(request.keyword());
    return toResponse(null, timetableMapper.findRecommendedTimetable(userId, maxCredits, keyword), true);
  }

  private String normalize(String value) {
    return value == null || value.trim().isEmpty() ? null : value.trim();
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
