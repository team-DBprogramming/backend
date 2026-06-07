package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.StudentHandler;
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
    Long studentId = requireStudentId(currentUser);
    List<StudentCartItem> items = cartMapper.findCartItems(studentId);
    int totalCredits = items.stream().map(StudentCartItem::getCredit).filter(v -> v != null).mapToInt(v -> v).sum();
    return new StudentCartListResponse(items, totalCredits, MAX_CREDITS);
  }

  @Transactional
  public StudentMutationResponse addCart(AuthenticatedUser currentUser, StudentCartAddRequest request) {
    Long studentId = requireStudentId(currentUser);
    Long sectionId = requireSectionId(request.courseId(), normalizeDivision(request.division()));
    cartMapper.insertCart(studentId, sectionId);
    Long cartId = cartMapper.findCartId(studentId, sectionId);
    return new StudentMutationResponse(String.valueOf(cartId), "ADDED");
  }

  @Transactional
  public StudentMutationResponse deleteCart(AuthenticatedUser currentUser, Long cartItemId) {
    Long studentId = requireStudentId(currentUser);
    int deleted = cartMapper.deleteCart(studentId, cartItemId);
    if (deleted == 0) {
      throw new StudentHandler(ErrorStatus.STUDENT_CART_NOT_FOUND);
    }
    return new StudentMutationResponse(String.valueOf(cartItemId), "DELETED");
  }

  @Transactional
  public StudentMutationResponse deleteCartByCourse(
      AuthenticatedUser currentUser, String courseId, String division) {
    Long studentId = requireStudentId(currentUser);
    Long sectionId = requireSectionId(courseId, normalizeDivision(division));
    int deleted = cartMapper.deleteCartBySection(studentId, sectionId);
    if (deleted == 0) {
      throw new StudentHandler(ErrorStatus.STUDENT_CART_NOT_FOUND);
    }
    return new StudentMutationResponse(String.valueOf(sectionId), "DELETED");
  }

  @Transactional
  public StudentBulkEnrollResponse bulkEnroll(AuthenticatedUser currentUser, StudentBulkEnrollRequest request) {
    Long studentId = requireStudentId(currentUser);
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

  private Long requireStudentId(AuthenticatedUser currentUser) {
    Long studentId = cartMapper.findStudentId(currentUser.requireStudentUserId());
    if (studentId == null) {
      throw new StudentHandler(ErrorStatus.STUDENT_NOT_FOUND);
    }
    return studentId;
  }

  private Long requireSectionId(String courseId, String division) {
    Long sectionId = cartMapper.findSectionId(courseId, division);
    if (sectionId == null) {
      throw new StudentHandler(ErrorStatus.STUDENT_COURSE_NOT_FOUND);
    }
    return sectionId;
  }
}
