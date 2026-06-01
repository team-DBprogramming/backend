package com.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationReplyMapper {

  Long findSenderUserId(@Param("recipientUserId") Long recipientUserId, @Param("notificationId") Long notificationId);

  void insertReply(
      @Param("recipientUserId") Long recipientUserId,
      @Param("senderUserId") Long senderUserId,
      @Param("notificationId") Long notificationId,
      @Param("message") String message);

  Long findLatestReplyId(@Param("recipientUserId") Long recipientUserId, @Param("senderUserId") Long senderUserId);
}
