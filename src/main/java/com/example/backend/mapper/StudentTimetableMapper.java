package com.example.backend.mapper;

import com.example.backend.dto.student.StudentTimetableItem;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentTimetableMapper {

  List<StudentTimetableItem> findEnrollmentTimetable(@Param("userId") Long userId);

  List<StudentTimetableItem> findCartTimetable(@Param("userId") Long userId);

  List<StudentTimetableItem> findRecommendedTimetable(
      @Param("userId") Long userId,
      @Param("maxCredits") Integer maxCredits,
      @Param("keyword") String keyword);
}
