package com.example.backend.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.exception.handler.NotificationHandler;
import com.example.backend.dto.notification.NotificationDetailResponse;
import com.example.backend.dto.notification.NotificationItem;
import com.example.backend.dto.notification.NotificationListResponse;
import com.example.backend.mapper.NotificationMapper;
import com.example.backend.service.NotificationService;
import java.util.ArrayList;
import java.util.List;
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
            "수강 요청",
            "학생으로부터 새 메시지가 도착했습니다.",
            "COURSE_REQUEST",
            false,
            "2026-06-01 09:30",
            "201",
            "301"));

    NotificationListResponse response = notificationService.getNotifications(10L);

    assertThat(notificationMapper.requestedRecipientUserId).isEqualTo(10L);
    assertThat(response.notifications()).hasSize(1);
    assertThat(response.notifications().get(0).notificationId()).isEqualTo("7");
    assertThat(response.notifications().get(0).isRead()).isFalse();
  }

  @Test
  void markAsReadUpdatesOnlyCurrentUserNotification() {
    notificationService.markAsRead(10L, "7");

    assertThat(notificationMapper.requestedRecipientUserId).isEqualTo(10L);
    assertThat(notificationMapper.requestedNotificationId).isEqualTo("7");
  }

  @Test
  void getNotificationReturnsCurrentUserNotificationDetail() {
    notificationMapper.detail =
        new NotificationDetailResponse(
            "7",
            "수강 요청",
            "학생으로부터 새 메시지가 도착했습니다.",
            "COURSE_REQUEST",
            false,
            "2026-06-01 09:30",
            "홍길동",
            "CSE301",
            "데이터베이스개론",
            "01분반",
            "201",
            "301",
            "전공 필수 과목으로 수강이 필요합니다.");

    NotificationDetailResponse response = notificationService.getNotification(10L, "7");

    assertThat(notificationMapper.requestedRecipientUserId).isEqualTo(10L);
    assertThat(notificationMapper.requestedNotificationId).isEqualTo("7");
    assertThat(response.senderName()).isEqualTo("홍길동");
    assertThat(response.courseName()).isEqualTo("데이터베이스개론");
    assertThat(response.requestReason()).isEqualTo("전공 필수 과목으로 수강이 필요합니다.");
  }

  @Test
  void getNotificationRejectsMissingOrOtherUserNotification() {
    assertThatThrownBy(() -> notificationService.getNotification(10L, "404"))
        .isInstanceOfSatisfying(
            NotificationHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode())
                    .isEqualTo("NOTIFICATION4041"));
  }

  private static class FakeNotificationMapper implements NotificationMapper {
    private Long requestedRecipientUserId;
    private String requestedNotificationId;
    private final List<NotificationItem> notifications = new ArrayList<>();
    private NotificationDetailResponse detail;

    @Override
    public List<NotificationItem> findNotifications(Long recipientUserId) {
      requestedRecipientUserId = recipientUserId;
      return notifications;
    }

    @Override
    public NotificationDetailResponse findNotification(Long recipientUserId, String notificationId) {
      requestedRecipientUserId = recipientUserId;
      requestedNotificationId = notificationId;
      return detail;
    }

    @Override
    public void markAsRead(Long recipientUserId, String notificationId) {
      requestedRecipientUserId = recipientUserId;
      requestedNotificationId = notificationId;
    }
  }
}
