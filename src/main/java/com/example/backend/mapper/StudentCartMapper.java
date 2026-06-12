package com.example.backend.mapper;

import com.example.backend.dto.student.StudentCartItem;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentCartMapper {

  Long findSectionId(@Param("courseId") String courseId, @Param("division") String division);

  List<StudentCartItem> findCartItems(@Param("studentId") String studentId);

  void callInsertCart(Map<String, Object> params);

  Long findCartId(
      @Param("studentId") String studentId,
      @Param("courseId") String courseId,
      @Param("sectionId") Long sectionId);

  void callDeleteCart(Map<String, Object> params);

  void callDeleteCartBySection(Map<String, Object> params);

  void callCountCart(Map<String, Object> params);

  void callEnrollFromCart(Map<String, Object> params);
}
