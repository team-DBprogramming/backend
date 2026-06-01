package com.example.backend.mapper;

import com.example.backend.dto.student.StudentCartItem;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentCartMapper {

  Long findStudentId(@Param("userId") Long userId);

  Long findSectionId(@Param("courseId") String courseId, @Param("division") String division);

  List<StudentCartItem> findCartItems(@Param("studentId") Long studentId);

  void insertCart(@Param("studentId") Long studentId, @Param("sectionId") Long sectionId);

  int deleteCart(@Param("studentId") Long studentId, @Param("cartItemId") Long cartItemId);

  int countCart(@Param("studentId") Long studentId);

  int insertEnrollmentsFromCart(@Param("studentId") Long studentId);

  int updateEnrolledCountsFromCart(@Param("studentId") Long studentId);
}
