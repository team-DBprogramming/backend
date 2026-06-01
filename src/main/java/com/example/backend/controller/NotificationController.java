package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.dto.notification.NotificationDetailResponse;
import com.example.backend.dto.notification.NotificationListResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "공통 알림 API")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping
  @Operation(summary = "알림 목록 조회", description = "현재 로그인한 사용자에게 발송된 알림 목록을 최신순으로 조회합니다.")
  public ApiResponse<NotificationListResponse> getNotifications(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
    return ApiResponse.of(
        SuccessStatus.NOTIFICATION_LIST,
        notificationService.getNotifications(userDetails.getUserId()));
  }

  @GetMapping("/{notificationId}")
  @Operation(summary = "알림 상세 조회", description = "알림 클릭 시 상세 화면 또는 메시지 팝업에 필요한 정보를 조회합니다.")
  public ApiResponse<NotificationDetailResponse> getNotification(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "알림 ID", example = "1") @PathVariable String notificationId) {
    return ApiResponse.of(
        SuccessStatus.NOTIFICATION_DETAIL,
        notificationService.getNotification(userDetails.getUserId(), notificationId));
  }

  @PatchMapping("/{notificationId}/read")
  @Operation(summary = "알림 읽음 처리", description = "현재 로그인한 사용자의 알림을 읽음 상태로 변경합니다.")
  public ApiResponse<Void> markAsRead(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "알림 ID", example = "1") @PathVariable String notificationId) {
    notificationService.markAsRead(userDetails.getUserId(), notificationId);
    return ApiResponse.of(SuccessStatus.NOTIFICATION_READ, null);
  }
}
