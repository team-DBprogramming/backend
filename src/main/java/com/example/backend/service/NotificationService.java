package com.example.backend.service;

import com.example.backend.dto.notification.NotificationDetailResponse;
import com.example.backend.dto.notification.NotificationListResponse;
import com.example.backend.mapper.NotificationMapper;
import com.example.backend.security.AuthenticatedUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationMapper notificationMapper;

  public NotificationService(NotificationMapper notificationMapper) {
    this.notificationMapper = notificationMapper;
  }

  @Transactional(readOnly = true)
  public NotificationListResponse getNotifications(AuthenticatedUser currentUser) {
    return new NotificationListResponse(notificationMapper.findNotifications(currentUser.requireStudentId()));
  }

  @Transactional(readOnly = true)
  public NotificationDetailResponse getNotification(AuthenticatedUser currentUser, String notificationId) {
    Map<String, Object> params = new HashMap<>();
    params.put("studentId", currentUser.requireStudentId());
    params.put("notificationId", notificationId);
    notificationMapper.callGetNotificationDetail(params);
    return firstDetail(params.get("result"));
  }

  @Transactional
  public void markAsRead(AuthenticatedUser currentUser, String notificationId) {
    notificationMapper.callMarkNotificationAsRead(currentUser.requireStudentId(), notificationId);
  }

  @SuppressWarnings("unchecked")
  private NotificationDetailResponse firstDetail(Object value) {
    if (value instanceof List<?> rows && !rows.isEmpty()) {
      return (NotificationDetailResponse) rows.get(0);
    }
    return null;
  }
}
