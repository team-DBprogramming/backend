package com.example.backend.service;

import com.example.backend.dto.student.StudentBulkEnrollResponse;
import com.example.backend.dto.student.StudentBulkEnrollRequest;
import com.example.backend.dto.student.StudentCartAddRequest;
import com.example.backend.dto.student.StudentCartItem;
import com.example.backend.dto.student.StudentCartListResponse;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.mapper.StudentCartMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentCartService {

  private final StudentCartMapper cartMapper;
  private static final int MAX_CREDITS = 21;

  public StudentCartService(StudentCartMapper cartMapper) {
    this.cartMapper = cartMapper;
  }

  @Transactional(readOnly = true)
  public StudentCartListResponse getCart(AuthenticatedUser currentUser) {
    Long studentId = cartMapper.findStudentId(currentUser.requireStudentUserId());
    List<StudentCartItem> items = cartMapper.findCartItems(studentId);
    int totalCredits = items.stream().map(StudentCartItem::getCredit).filter(v -> v != null).mapToInt(v -> v).sum();
    return new StudentCartListResponse(items, totalCredits, MAX_CREDITS);
  }

  @Transactional
  public StudentMutationResponse addCart(AuthenticatedUser currentUser, StudentCartAddRequest request) {
    Long studentId = cartMapper.findStudentId(currentUser.requireStudentUserId());
    Long sectionId = cartMapper.findSectionId(request.courseId(), normalizeDivision(request.division()));
    cartMapper.insertCart(studentId, sectionId);
    return new StudentMutationResponse(String.valueOf(sectionId), "ADDED");
  }

  @Transactional
  public StudentMutationResponse deleteCart(AuthenticatedUser currentUser, Long cartItemId) {
    Long studentId = cartMapper.findStudentId(currentUser.requireStudentUserId());
    cartMapper.deleteCart(studentId, cartItemId);
    return new StudentMutationResponse(String.valueOf(cartItemId), "DELETED");
  }

  @Transactional
  public StudentBulkEnrollResponse bulkEnroll(AuthenticatedUser currentUser, StudentBulkEnrollRequest request) {
    Long studentId = cartMapper.findStudentId(currentUser.requireStudentUserId());
    List<Long> cartItemIds = request == null ? null : request.cartItemIds();
    int requested =
        cartItemIds == null || cartItemIds.isEmpty()
            ? cartMapper.countCart(studentId)
            : cartMapper.countSelectedCart(studentId, cartItemIds);
    int enrolled = cartMapper.insertEnrollmentsFromCart(studentId, cartItemIds);
    cartMapper.updateEnrolledCountsFromCart(studentId, cartItemIds);
    return new StudentBulkEnrollResponse(
        List.of(
            new StudentBulkEnrollResponse.Result(
                null,
                enrolled > 0,
                enrolled > 0 ? "수강신청 성공" : "신청 가능한 장바구니 항목이 없습니다",
                enrolled > 0 ? null : "NOT_APPLICABLE")),
        new StudentBulkEnrollResponse.Summary(requested, enrolled, Math.max(0, requested - enrolled)));
  }

  private String normalizeDivision(String division) {
    return division == null || division.trim().isEmpty() ? null : division.replace("분반", "").trim();
  }
}
