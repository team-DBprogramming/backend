package com.example.backend.mapper;

import com.example.backend.dto.notification.NotificationItem;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper {

  List<NotificationItem> findNotifications(
      @Param("studentId") String studentId);

  void callGetNotificationDetail(Map<String, Object> params);

  void callMarkNotificationAsRead(
      @Param("studentId") String studentId,
      @Param("notificationId") String notificationId);
}
