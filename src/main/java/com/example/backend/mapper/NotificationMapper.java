package com.example.backend.mapper;

import com.example.backend.dto.notification.NotificationItem;
import com.example.backend.dto.notification.NotificationDetailResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper {

  List<NotificationItem> findNotifications(@Param("recipientUserId") Long recipientUserId);

  NotificationDetailResponse findNotification(
      @Param("recipientUserId") Long recipientUserId,
      @Param("notificationId") String notificationId);

  void markAsRead(
      @Param("recipientUserId") Long recipientUserId,
      @Param("notificationId") String notificationId);
}
