package com.example.backend.dto.notification;

public record NotificationItem(
    String notificationId,
    String title,
    String body,
    String type,
    boolean isRead,
    String createdAt,
    String targetSectionId,
    String targetRequestId) {}
