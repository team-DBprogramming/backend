package com.example.backend.service;

import com.example.backend.dto.student.StudentBorrowRequest;
import com.example.backend.dto.student.StudentCourseDetailResponse;
import com.example.backend.dto.student.StudentCourseReviewItem;
import com.example.backend.dto.student.StudentCourseListResponse;
import com.example.backend.dto.student.StudentCourseSchedule;
import com.example.backend.dto.student.StudentCourseSummary;
import com.example.backend.dto.student.StudentLectureTime;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.mapper.StudentCourseMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentCourseService {

  private final StudentCourseMapper courseMapper;

  public StudentCourseService(StudentCourseMapper courseMapper) {
    this.courseMapper = courseMapper;
  }

  @Transactional(readOnly = true)
  public StudentCourseListResponse getCourses(
      String semester,
      String keyword,
      String courseCategory,
      String major,
      String courseType,
      List<String> days,
      List<Integer> credits,
      String startTime,
      String endTime,
      String sort,
      Integer page,
      Integer size) {
    int normalizedPage = page == null || page < 1 ? 1 : page;
    int normalizedSize = size == null || size < 1 ? 20 : size;
    int offset = (normalizedPage - 1) * normalizedSize;
    List<String> normalizedDays = normalizeStrings(days);
    List<Integer> normalizedCredits = normalizeIntegers(credits);
    Integer total =
        courseMapper.countCourses(
            normalize(semester),
            normalize(keyword),
            normalize(courseCategory),
            normalize(major),
            normalize(courseType),
            normalizedDays,
            normalizedCredits,
            normalize(startTime),
            normalize(endTime));
    List<StudentCourseSummary> courses =
        courseMapper.findCourses(
            normalize(semester),
            normalize(keyword),
            normalize(courseCategory),
            normalize(major),
            normalize(courseType),
            normalizedDays,
            normalizedCredits,
            normalize(startTime),
            normalize(endTime),
            normalize(sort),
            offset,
            normalizedSize);
    courses.forEach(course -> course.setLectureTimes(toLectureTimes(
        courseMapper.findSchedules(course.getCourseId(), normalizeDivision(course.getDivision())))));
    return new StudentCourseListResponse(
        courses,
        new StudentCourseListResponse.Pagination(normalizedPage, normalizedSize, total == null ? 0 : total));
  }

  @Transactional(readOnly = true)
  public StudentCourseDetailResponse getCourse(String courseId, String division) {
    String normalizedDivision = normalizeDivision(division);
    StudentCourseSummary course = courseMapper.findCourse(courseId, normalizedDivision);
    List<StudentCourseSchedule> schedules = courseMapper.findSchedules(courseId, normalizedDivision);
    List<StudentCourseReviewItem> reviews = courseMapper.findCourseReviews(courseId, normalizedDivision);
    boolean enrollable = course != null && course.getCapacity() != null && course.getEnrolled() != null
        && course.getEnrolled() < course.getCapacity();
    return new StudentCourseDetailResponse(
        new StudentCourseDetailResponse.Overview(
            course == null ? null : course.getCourseName(),
            course == null ? courseId : course.getCourseId(),
            course == null ? null : course.getProfessor(),
            course == null ? null : course.getCourseType(),
            course == null ? null : course.getCredit(),
            schedules.stream()
                .map(schedule -> new StudentLectureTime(schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime()))
                .toList(),
            course == null ? null : course.getRoom(),
            course == null ? null : course.getCapacity(),
            course == null ? null : course.getEnrolled(),
            course == null ? null : course.getSeatStatus(),
            course == null ? null : normalizeDivision(course.getDivision()),
            course == null ? null : course.getYear()),
        new StudentCourseDetailResponse.Syllabus(
            null,
            new StudentCourseDetailResponse.GradingCriteria(30, 30, 30, 10),
            null,
            courseMapper.findProfessorEmail(courseId, normalizedDivision),
            courseMapper.findProfessorPhone(courseId, normalizedDivision),
            courseMapper.findPrerequisite(courseId),
            courseMapper.findDescription(courseId)),
        new StudentCourseDetailResponse.EnrollmentEligibility(
            enrollable,
            enrollable ? null : "CAPACITY_FULL",
            enrollable ? "수강신청이 가능합니다" : "정원이 마감되었습니다",
            false,
            !enrollable),
        new StudentCourseDetailResponse.RequestInfo(
            !enrollable,
            enrollable ? null : "정원 초과로 인해 수강 요청 가능",
            "NONE",
            false),
        reviews);
  }

  @Transactional
  public StudentMutationResponse requestBorrow(
      AuthenticatedUser currentUser, String courseId, StudentBorrowRequest request) {
    Long userId = currentUser.requireStudentUserId();
    Long studentId = courseMapper.findStudentId(userId);
    Long sectionId = courseMapper.findSectionId(courseId, normalizeDivision(request.division()));
    courseMapper.insertBorrowRequest(studentId, sectionId, valueOrDefault(request.reason(), "수강을 희망합니다."));
    Long requestId = courseMapper.findLatestBorrowRequestId(studentId, sectionId);
    return new StudentMutationResponse(String.valueOf(requestId), "PENDING");
  }

  private String normalizeDivision(String division) {
    return isBlank(division) ? null : division.replace("분반", "").trim();
  }

  private String normalize(String value) {
    return isBlank(value) ? null : value.trim();
  }

  private String valueOrDefault(String value, String defaultValue) {
    return isBlank(value) ? defaultValue : value.trim();
  }

  private List<StudentLectureTime> toLectureTimes(List<StudentCourseSchedule> schedules) {
    return schedules.stream()
        .map(schedule -> new StudentLectureTime(schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime()))
        .toList();
  }

  private List<String> normalizeStrings(List<String> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .map(this::normalize)
        .filter(value -> value != null)
        .toList();
  }

  private List<Integer> normalizeIntegers(List<Integer> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream().filter(value -> value != null).toList();
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
