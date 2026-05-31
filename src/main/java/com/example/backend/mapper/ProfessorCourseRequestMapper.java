package com.example.backend.mapper;

import com.example.backend.dto.professor.CourseRequestItem;
import com.example.backend.dto.professor.CourseRequestSummary;
import com.example.backend.dto.professor.ProfessorCourseRequestInfo;
import java.time.Instant;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorCourseRequestMapper {

  ProfessorCourseRequestInfo findRequestForProfessor(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("requestId") String requestId);

  CourseRequestSummary findRequestSummary(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division);

  List<CourseRequestItem> findPendingRequests(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("size") int size,
      @Param("offset") int offset);

  int updatePendingRequestStatus(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division,
      @Param("requestId") String requestId,
      @Param("status") String status,
      @Param("processedAt") Instant processedAt);

  void insertResultNotification(
      @Param("recipientUserId") Long recipientUserId,
      @Param("senderUserId") Long senderUserId,
      @Param("targetSectionId") Long targetSectionId,
      @Param("targetRequestId") Long targetRequestId,
      @Param("title") String title,
      @Param("body") String body,
      @Param("type") String type,
      @Param("createdAt") Instant createdAt);
}
