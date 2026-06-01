package com.example.backend.mapper;

import com.example.backend.dto.professor.ProfessorMessageRecipient;
import java.time.Instant;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorMessageMapper {

  Long findTargetSectionId(
      @Param("professorUserId") Long professorUserId,
      @Param("courseId") String courseId,
      @Param("division") String division);

  List<ProfessorMessageRecipient> findRecipients(
      @Param("sectionId") Long sectionId, @Param("studentIds") List<String> studentIds);

  void insertProfessorMessageNotification(
      @Param("recipientUserId") Long recipientUserId,
      @Param("senderUserId") Long senderUserId,
      @Param("targetSectionId") Long targetSectionId,
      @Param("title") String title,
      @Param("body") String body,
      @Param("type") String type,
      @Param("createdAt") Instant createdAt);
}
