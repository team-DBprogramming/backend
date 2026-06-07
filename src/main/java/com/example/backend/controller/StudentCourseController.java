package com.example.backend.controller;

import com.example.backend.dto.student.StudentApiResponse;
import com.example.backend.dto.student.StudentBorrowRequest;
import com.example.backend.dto.student.StudentCartAddRequest;
import com.example.backend.dto.student.StudentCourseDetailResponse;
import com.example.backend.dto.student.StudentCourseListResponse;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.StudentCartService;
import com.example.backend.service.StudentCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@Tag(name = "Student Courses", description = "학생 강의 검색, 상세, 빌넣 신청 API")
public class StudentCourseController {

  private final StudentCourseService courseService;
  private final StudentCartService cartService;

  public StudentCourseController(StudentCourseService courseService, StudentCartService cartService) {
    this.courseService = courseService;
    this.cartService = cartService;
  }

  @GetMapping
  @Operation(summary = "강의 목록 검색", description = "학기, 키워드, 분류, 전공, 유형, 요일, 시간, 학점 조건으로 강의를 검색합니다.")
  public StudentApiResponse<StudentCourseListResponse> getCourses(
      @RequestParam("semester") String semester,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "courseCategory", required = false) String courseCategory,
      @RequestParam(value = "major", required = false) String major,
      @RequestParam(value = "courseType", required = false) String courseType,
      @RequestParam(value = "day", required = false) List<String> day,
      @RequestParam(value = "credit", required = false) List<Integer> credit,
      @RequestParam(value = "startTime", required = false) String startTime,
      @RequestParam(value = "endTime", required = false) String endTime,
      @RequestParam(value = "sort", required = false) String sort,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    return StudentApiResponse.success(
        "S200",
        "강의 목록 조회 성공",
        courseService.getCourses(
            semester, keyword, courseCategory, major, courseType, day, credit, startTime, endTime, sort, page, size));
  }

  @GetMapping("/{courseId}")
  @Operation(summary = "강의 상세 조회", description = "강의 코드 기준으로 현재 학기 강의 상세 정보를 조회합니다.")
  public StudentApiResponse<StudentCourseDetailResponse> getCourse(
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @RequestParam(value = "division", required = false) String division) {
    return StudentApiResponse.success(
        "S200", "강의 상세 조회 성공", courseService.getCourse(courseId, division));
  }

  @PostMapping("/{courseId}/borrow-request")
  @Operation(summary = "빌넣 신청", description = "정원이 찬 강의 또는 수강 희망 강의에 빌넣 요청을 등록합니다.")
  public StudentApiResponse<StudentMutationResponse> requestBorrow(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @RequestBody StudentBorrowRequest request) {
    return StudentApiResponse.success(
        "S201",
        "빌넣 신청 성공",
        courseService.requestBorrow(userDetails.toAuthenticatedUser(), courseId, request));
  }

  @PostMapping({"/{courseId}/cart", "/{courseId}/scrap"})
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "강의 찜 추가", description = "강의를 현재 로그인한 학생의 장바구니에 추가합니다.")
  public StudentApiResponse<StudentMutationResponse> addCourseCart(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @RequestParam(value = "division", required = false) String division) {
    return StudentApiResponse.success(
        "S201",
        "강의 찜 추가 성공",
        cartService.addCart(userDetails.toAuthenticatedUser(), new StudentCartAddRequest(courseId, division)));
  }

  @DeleteMapping({"/{courseId}/cart", "/{courseId}/scrap"})
  @Operation(summary = "강의 찜 해제", description = "강의를 현재 로그인한 학생의 장바구니에서 제거합니다.")
  public StudentApiResponse<StudentMutationResponse> deleteCourseCart(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "강의 ID", example = "CSE301") @PathVariable String courseId,
      @RequestParam(value = "division", required = false) String division) {
    return StudentApiResponse.success(
        "S200",
        "강의 찜 해제 성공",
        cartService.deleteCartByCourse(userDetails.toAuthenticatedUser(), courseId, division));
  }
}
