package com.example.backend.dto.notification;

public record NotificationDetailResponse(
    String notificationId,
    String title,
    String body,
    String type,
    boolean isRead,
    String createdAt,
    String senderName,
    String courseId,
    String courseName,
    String division,
    String targetSectionId,
    String targetRequestId,
    String requestReason) {}
