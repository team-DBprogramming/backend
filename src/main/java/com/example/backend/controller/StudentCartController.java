package com.example.backend.controller;

import com.example.backend.dto.student.StudentApiResponse;
import com.example.backend.dto.student.StudentBulkEnrollRequest;
import com.example.backend.dto.student.StudentBulkEnrollResponse;
import com.example.backend.dto.student.StudentCartAddRequest;
import com.example.backend.dto.student.StudentCartListResponse;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.StudentCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students/me/cart")
@Tag(name = "Student Cart", description = "학생 장바구니 API")
public class StudentCartController {

  private final StudentCartService cartService;

  public StudentCartController(StudentCartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping
  @Operation(summary = "장바구니 조회", description = "현재 로그인한 학생의 현재 학기 장바구니 목록을 조회합니다.")
  public StudentApiResponse<StudentCartListResponse> getCart(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
    return StudentApiResponse.success(
        "S200", "장바구니 조회 성공", cartService.getCart(userDetails.toAuthenticatedUser()));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "장바구니 등록", description = "강의를 장바구니에 등록합니다.")
  public StudentApiResponse<StudentMutationResponse> addCart(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody StudentCartAddRequest request) {
    return StudentApiResponse.success(
        "S201", "장바구니 등록 성공", cartService.addCart(userDetails.toAuthenticatedUser(), request));
  }

  @DeleteMapping("/{cartItemId}")
  @Operation(summary = "장바구니 삭제", description = "장바구니 항목을 삭제합니다.")
  public StudentApiResponse<StudentMutationResponse> deleteCart(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long cartItemId) {
    return StudentApiResponse.success(
        "S200", "장바구니 삭제 성공", cartService.deleteCart(userDetails.toAuthenticatedUser(), cartItemId));
  }

  @PostMapping("/bulk-enroll")
  @Operation(summary = "장바구니 일괄 수강신청", description = "장바구니에 담긴 강의를 일괄 수강신청합니다.")
  public StudentApiResponse<StudentBulkEnrollResponse> bulkEnroll(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody(required = false) StudentBulkEnrollRequest request) {
    return StudentApiResponse.success(
        "S200", "장바구니 일괄 수강신청 성공", cartService.bulkEnroll(userDetails.toAuthenticatedUser(), request));
  }
}
