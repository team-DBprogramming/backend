package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.NotificationHandler;
import com.example.backend.dto.notification.NotificationDetailResponse;
import com.example.backend.dto.notification.NotificationListResponse;
import com.example.backend.mapper.NotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationMapper notificationMapper;

  public NotificationService(NotificationMapper notificationMapper) {
    this.notificationMapper = notificationMapper;
  }

  @Transactional(readOnly = true)
  public NotificationListResponse getNotifications(Long recipientUserId) {
    return new NotificationListResponse(notificationMapper.findNotifications(recipientUserId));
  }

  @Transactional(readOnly = true)
  public NotificationDetailResponse getNotification(Long recipientUserId, String notificationId) {
    NotificationDetailResponse notification =
        notificationMapper.findNotification(recipientUserId, notificationId);
    if (notification == null) {
      throw new NotificationHandler(ErrorStatus.NOTIFICATION_NOT_FOUND);
    }
    return notification;
  }

  @Transactional
  public void markAsRead(Long recipientUserId, String notificationId) {
    notificationMapper.markAsRead(recipientUserId, notificationId);
  }
}
