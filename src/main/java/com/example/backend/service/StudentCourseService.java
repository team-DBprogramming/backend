package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.StudentHandler;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      String courseMajor,
      String courseType,
      List<String> days,
      Integer targetYear,
      Boolean isEnglish,
      List<Integer> credits,
      String startTime,
      String endTime,
      Boolean hasSeatMargin,
      Boolean highReview,
      String sort,
      Integer page,
      Integer size) {
    int normalizedPage = page == null || page < 1 ? 1 : page;
    int normalizedSize = size == null || size < 1 ? 20 : size;
    int offset = (normalizedPage - 1) * normalizedSize;
    List<String> normalizedDays = normalizeStrings(days);
    List<Integer> normalizedCredits = normalizeIntegers(credits);
    String normalizedMajor = normalize(firstNonBlank(courseMajor, firstNonBlank(major, courseCategory)));
    Integer total =
        courseMapper.countCourses(
            normalize(keyword),
            normalizedMajor,
            normalize(courseType),
            normalizedDays,
            targetYear,
            isEnglish,
            normalizedCredits,
            normalize(startTime),
            normalize(endTime),
            hasSeatMargin,
            highReview);
    List<StudentCourseSummary> courses =
        courseMapper.findCourses(
            normalize(keyword),
            normalizedMajor,
            normalize(courseType),
            normalizedDays,
            targetYear,
            isEnglish,
            normalizedCredits,
            normalize(startTime),
            normalize(endTime),
            hasSeatMargin,
            highReview,
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
  public StudentCourseDetailResponse getCourse(
      AuthenticatedUser currentUser, String courseId, String division) {
    String studentId = currentUser.requireStudentId();
    String normalizedDivision = normalizeDivision(division);
    StudentCourseSummary course = courseMapper.findCourse(courseId, normalizedDivision);
    if (course == null) {
      throw new StudentHandler(ErrorStatus.STUDENT_COURSE_NOT_FOUND);
    }
    String courseDivision = normalizeDivision(course.getDivision());
    Map<String, Object> detailFields = courseMapper.findCourseDetailFields(courseId);
    List<StudentCourseSchedule> schedules = courseMapper.findSchedules(courseId, normalizedDivision);
    List<StudentCourseReviewItem> reviews = courseMapper.findCourseReviews(courseId, normalizedDivision);
    boolean isEnrolled = intValue(courseMapper.countCurrentEnrollment(studentId, courseId, courseDivision)) > 0;
    String requestStatus = valueOrDefault(
        courseMapper.findBorrowRequestStatus(studentId, courseId, courseDivision),
        "NONE");
    boolean hasSeat = course.getCapacity() != null && course.getEnrolled() != null
        && course.getEnrolled() < course.getCapacity();
    boolean enrollable = hasSeat && !isEnrolled;
    boolean requestable = !isEnrolled && !hasSeat && !"PENDING".equals(requestStatus);
    return new StudentCourseDetailResponse(
        new StudentCourseDetailResponse.Overview(
            course.getCourseName(),
            course.getCourseId(),
            course.getProfessor(),
            course.getCourseType(),
            course.getCredit(),
            schedules.stream()
                .map(schedule -> new StudentLectureTime(schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime()))
                .toList(),
            course.getRoom(),
            course.getCapacity(),
            course.getEnrolled(),
            course.getSeatStatus(),
            course.getDivision(),
            course.getYear()),
        new StudentCourseDetailResponse.Syllabus(
            stringValue(detailFields, "textbook"),
            new StudentCourseDetailResponse.GradingCriteria(
                intValue(detailFields, "midterm_rate"),
                intValue(detailFields, "final_rate"),
                intValue(detailFields, "assignment_rate"),
                intValue(detailFields, "attendance_rate")),
            intValue(detailFields, "assignment_count"),
            courseMapper.findProfessorEmail(courseId, normalizedDivision),
            courseMapper.findProfessorPhone(courseId, normalizedDivision),
            stringValue(detailFields, "prerequisite"),
            stringValue(detailFields, "note")),
        new StudentCourseDetailResponse.EnrollmentEligibility(
            enrollable,
            eligibilityReasonCode(isEnrolled, hasSeat),
            eligibilityMessage(isEnrolled, hasSeat),
            isEnrolled,
            requestable),
        new StudentCourseDetailResponse.RequestInfo(
            requestable,
            requestable ? "정원 초과로 인해 수강 요청 가능" : null,
            requestStatus,
            isEnrolled),
        reviews);
  }

  @Transactional
  public StudentMutationResponse requestBorrow(
      AuthenticatedUser currentUser, String courseId, StudentBorrowRequest request) {
    String studentId = currentUser.requireStudentId();
    Long sectionId = courseMapper.findSectionId(courseId, normalizeDivision(request.division()));
    if (sectionId == null) {
      throw new StudentHandler(ErrorStatus.STUDENT_COURSE_NOT_FOUND);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    params.put("courseId", courseId);
    params.put("sectionId", sectionId);
    params.put("reason", valueOrDefault(request.reason(), "수강을 희망합니다."));
    courseMapper.callInsertBorrowRequest(params);
    return new StudentMutationResponse(String.valueOf(params.get("requestId")), String.valueOf(params.get("result")));
  }

  private String eligibilityReasonCode(boolean isEnrolled, boolean hasSeat) {
    if (isEnrolled) {
      return "ALREADY_ENROLLED";
    }
    return hasSeat ? null : "CAPACITY_FULL";
  }

  private String eligibilityMessage(boolean isEnrolled, boolean hasSeat) {
    if (isEnrolled) {
      return "이미 수강신청한 강의입니다";
    }
    return hasSeat ? "수강신청이 가능합니다" : "정원이 마감되었습니다";
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

  private String stringValue(Map<String, Object> values, String key) {
    Object value = mapValue(values, key);
    return value == null ? null : String.valueOf(value);
  }

  private Integer intValue(Map<String, Object> values, String key) {
    return intValue(mapValue(values, key));
  }

  private Integer intValue(Object value) {
    if (value == null) {
      return 0;
    }
    if (value instanceof Number number) {
      return number.intValue();
    }
    return Integer.parseInt(String.valueOf(value));
  }

  private Object mapValue(Map<String, Object> values, String key) {
    if (values == null) {
      return null;
    }
    if (values.containsKey(key)) {
      return values.get(key);
    }
    return values.get(key.toUpperCase());
  }

  private String firstNonBlank(String first, String second) {
    return isBlank(first) ? second : first;
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
