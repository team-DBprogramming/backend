package com.example.backend.mapper;

import com.example.backend.dto.notification.NotificationItem;
import com.example.backend.dto.notification.NotificationDetailResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper {

  List<NotificationItem> findNotifications(
      @Param("studentId") String studentId);

  NotificationDetailResponse findNotification(
      @Param("studentId") String studentId,
      @Param("notificationId") String notificationId);

  void markAsRead(
      @Param("studentId") String studentId,
      @Param("notificationId") String notificationId);
}
