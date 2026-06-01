package com.example.backend.service;

import com.example.backend.dto.student.NotificationReplyRequest;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.mapper.NotificationReplyMapper;
import com.example.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationReplyService {

  private final NotificationReplyMapper replyMapper;

  public NotificationReplyService(NotificationReplyMapper replyMapper) {
    this.replyMapper = replyMapper;
  }

  @Transactional
  public StudentMutationResponse reply(
      AuthenticatedUser currentUser, String notificationId, NotificationReplyRequest request) {
    Long senderUserId = currentUser.userId();
    Long recipientUserId = replyMapper.findSenderUserId(senderUserId, Long.valueOf(notificationId));
    replyMapper.insertReply(recipientUserId, senderUserId, Long.valueOf(notificationId), request.message());
    Long replyId = replyMapper.findLatestReplyId(recipientUserId, senderUserId);
    return new StudentMutationResponse(String.valueOf(replyId), "SENT");
  }
}
