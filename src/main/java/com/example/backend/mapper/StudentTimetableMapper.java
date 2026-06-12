package com.example.backend.mapper;

import com.example.backend.dto.student.StudentTimetableItem;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentTimetableMapper {

  void callGetEnrollmentTimetable(Map<String, Object> params);

  List<StudentTimetableItem> findCartTimetable(@Param("studentId") String studentId);
}
