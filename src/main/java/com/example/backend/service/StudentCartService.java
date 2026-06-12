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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String studentId = currentUser.requireStudentId();
    List<StudentCartItem> items = cartMapper.findCartItems(studentId);
    int totalCredits = items.stream().map(StudentCartItem::getCredit).filter(v -> v != null).mapToInt(v -> v).sum();
    return new StudentCartListResponse(items, totalCredits, MAX_CREDITS);
  }

  @Transactional
  public StudentMutationResponse addCart(AuthenticatedUser currentUser, StudentCartAddRequest request) {
    String studentId = currentUser.requireStudentId();
    Long sectionId = requireSectionId(request.courseId(), normalizeDivision(request.division()));
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    params.put("courseId", request.courseId());
    params.put("sectionId", sectionId);
    cartMapper.callInsertCart(params);
    String result = stringValue(params.get("result"));
    if ("CLASS_NOT_FOUND".equals(result)) {
      throw new StudentHandler(ErrorStatus.STUDENT_COURSE_NOT_FOUND);
    }
    Long cartId = cartMapper.findCartId(studentId, request.courseId(), sectionId);
    return new StudentMutationResponse(String.valueOf(cartId), "ALREADY_EXISTS".equals(result) ? result : "ADDED");
  }

  @Transactional
  public StudentMutationResponse deleteCart(AuthenticatedUser currentUser, Long cartItemId) {
    String studentId = currentUser.requireStudentId();
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    params.put("cartItemId", cartItemId);
    cartMapper.callDeleteCart(params);
    if ("NOT_FOUND".equals(params.get("result"))) {
      throw new StudentHandler(ErrorStatus.STUDENT_CART_NOT_FOUND);
    }
    return new StudentMutationResponse(String.valueOf(cartItemId), "DELETED");
  }

  @Transactional
  public StudentMutationResponse deleteCartByCourse(
      AuthenticatedUser currentUser, String courseId, String division) {
    String studentId = currentUser.requireStudentId();
    Long sectionId = requireSectionId(courseId, normalizeDivision(division));
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    params.put("courseId", courseId);
    params.put("sectionId", sectionId);
    cartMapper.callDeleteCartBySection(params);
    if ("NOT_FOUND".equals(params.get("result"))) {
      throw new StudentHandler(ErrorStatus.STUDENT_CART_NOT_FOUND);
    }
    return new StudentMutationResponse(String.valueOf(sectionId), "DELETED");
  }

  @Transactional
  public StudentBulkEnrollResponse bulkEnroll(AuthenticatedUser currentUser, StudentBulkEnrollRequest request) {
    String studentId = currentUser.requireStudentId();
    List<StudentCartItem> cartItems = cartMapper.findCartItems(studentId);
    List<String> courseIds = selectedCourseIds(request, cartItems);
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    params.put("courseIdsCsv", toCsv(courseIds));
    cartMapper.callEnrollFromCart(params);
    List<Map<String, Object>> resultRows = mapList(params.get("results"));
    Map<String, Object> summaryRow = firstMap(params.get("summary"));
    return new StudentBulkEnrollResponse(
        resultRows.stream()
            .map(row -> new StudentBulkEnrollResponse.Result(
                stringValue(row.get("cartItemId")),
                stringValue(row.get("courseId")),
                intValue(row.get("success")) == 1,
                stringValue(row.get("message")),
                stringValue(row.get("code"))))
            .toList(),
        new StudentBulkEnrollResponse.Summary(
            intValue(summaryRow.get("total")),
            intValue(summaryRow.get("success")),
            intValue(summaryRow.get("failed"))));
  }

  private List<String> selectedCourseIds(StudentBulkEnrollRequest request, List<StudentCartItem> cartItems) {
    if (request != null && request.courseIds() != null && !request.courseIds().isEmpty()) {
      return request.courseIds().stream()
          .filter(value -> value != null && !value.trim().isEmpty())
          .map(String::trim)
          .toList();
    }
    if (request != null && request.cartItemIds() != null && !request.cartItemIds().isEmpty()) {
      return cartItems.stream()
          .filter(item -> request.cartItemIds().contains(item.getCartItemId()))
          .map(StudentCartItem::getCourseId)
          .toList();
    }
    return cartItems.stream().map(StudentCartItem::getCourseId).toList();
  }

  private String normalizeDivision(String division) {
    return division == null || division.trim().isEmpty() ? null : division.replace("분반", "").trim();
  }

  private Long requireSectionId(String courseId, String division) {
    Long sectionId = cartMapper.findSectionId(courseId, division);
    if (sectionId == null) {
      throw new StudentHandler(ErrorStatus.STUDENT_COURSE_NOT_FOUND);
    }
    return sectionId;
  }

  private int countCart(String studentId) {
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", studentId);
    cartMapper.callCountCart(params);
    return intValue(params.get("count"));
  }

  private String toCsv(List<?> values) {
    return values.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> mapList(Object value) {
    return value instanceof List<?> list ? (List<Map<String, Object>>) list : List.of();
  }

  private Map<String, Object> firstMap(Object value) {
    List<Map<String, Object>> rows = mapList(value);
    return rows.isEmpty() ? Map.of("total", 0, "success", 0, "failed", 0) : rows.get(0);
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

  private String stringValue(Object value) {
    return value == null ? null : String.valueOf(value);
  }
}
