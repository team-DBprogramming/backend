package com.example.backend.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.backend.dto.notification.NotificationDetailResponse;
import com.example.backend.dto.notification.NotificationItem;
import com.example.backend.dto.notification.NotificationListResponse;
import com.example.backend.mapper.NotificationMapper;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.NotificationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {

  private FakeNotificationMapper notificationMapper;
  private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    notificationMapper = new FakeNotificationMapper();
    notificationService = new NotificationService(notificationMapper);
  }

  @Test
  void getNotificationsReturnsCurrentUserNotifications() {
    notificationMapper.notifications.add(
        new NotificationItem(
            "7",
            "Course request",
            "A student sent a message.",
            "COURSE_REQUEST",
            false,
            "2026-06-01 09:30",
            "CSE3033",
            "1",
            "301"));

    NotificationListResponse response = notificationService.getNotifications(studentUser());

    assertThat(notificationMapper.requestedStudentId).isEqualTo("20230001");
    assertThat(response.notifications()).hasSize(1);
    assertThat(response.notifications().get(0).notificationId()).isEqualTo("7");
    assertThat(response.notifications().get(0).isRead()).isFalse();
  }

  @Test
  void markAsReadUpdatesOnlyCurrentUserNotification() {
    notificationService.markAsRead(studentUser(), "7");

    assertThat(notificationMapper.requestedStudentId).isEqualTo("20230001");
    assertThat(notificationMapper.requestedNotificationId).isEqualTo("7");
  }

  @Test
  void getNotificationReturnsCurrentUserNotificationDetail() {
    notificationMapper.detail =
        new NotificationDetailResponse(
            "7",
            "Course request",
            "A student sent a message.",
            "COURSE_REQUEST",
            false,
            "2026-06-01 09:30",
            "Sender",
            "CSE301",
            "Database",
            "01",
            "CSE3033",
            "1",
            "301",
            "Required major course.");

    NotificationDetailResponse response = notificationService.getNotification(studentUser(), "7");

    assertThat(notificationMapper.requestedStudentId).isEqualTo("20230001");
    assertThat(notificationMapper.requestedNotificationId).isEqualTo("7");
    assertThat(response.senderName()).isEqualTo("Sender");
    assertThat(response.courseName()).isEqualTo("Database");
    assertThat(response.requestReason()).isEqualTo("Required major course.");
  }

  @Test
  void getNotificationUsesCallableProcedureResult() {
    notificationMapper.detail =
        new NotificationDetailResponse(
            "7",
            "Course request",
            "A student sent a message.",
            "COURSE_REQUEST",
            false,
            "2026-06-01 09:30",
            "Sender",
            "CSE301",
            "Database",
            "01",
            "CSE3033",
            "1",
            "301",
            "Required major course.");

    NotificationDetailResponse response = notificationService.getNotification(studentUser(), "7");

    assertThat(notificationMapper.requestedStudentId).isEqualTo("20230001");
    assertThat(notificationMapper.requestedNotificationId).isEqualTo("7");
    assertThat(response.notificationId()).isEqualTo("7");
  }

  private static class FakeNotificationMapper implements NotificationMapper {
    private String requestedStudentId;
    private String requestedNotificationId;
    private final List<NotificationItem> notifications = new ArrayList<>();
    private NotificationDetailResponse detail;

    @Override
    public List<NotificationItem> findNotifications(String studentId) {
      requestedStudentId = studentId;
      return notifications;
    }

    @Override
    public void callGetNotificationDetail(Map<String, Object> params) {
      requestedStudentId = String.valueOf(params.get("studentId"));
      requestedNotificationId = String.valueOf(params.get("notificationId"));
      if (detail != null) {
        params.put("result", List.of(detail));
      }
    }

    @Override
    public void callMarkNotificationAsRead(String studentId, String notificationId) {
      requestedStudentId = studentId;
      requestedNotificationId = notificationId;
    }
  }

  private AuthenticatedUser studentUser() {
    return new AuthenticatedUser(10L, "20230001", "STUDENT");
  }
}
