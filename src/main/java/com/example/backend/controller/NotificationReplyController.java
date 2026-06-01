package com.example.backend.controller;

import com.example.backend.dto.student.NotificationReplyRequest;
import com.example.backend.dto.student.StudentApiResponse;
import com.example.backend.dto.student.StudentMutationResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.NotificationReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Replies", description = "알림 답장 API")
public class NotificationReplyController {

  private final NotificationReplyService replyService;

  public NotificationReplyController(NotificationReplyService replyService) {
    this.replyService = replyService;
  }

  @PostMapping("/{notificationId}/reply")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "알림 답장", description = "받은 알림의 발신자에게 답장 알림을 전송합니다.")
  public StudentApiResponse<StudentMutationResponse> reply(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable String notificationId,
      @RequestBody NotificationReplyRequest request) {
    return StudentApiResponse.success(
        "S201", "알림 답장 성공", replyService.reply(userDetails.toAuthenticatedUser(), notificationId, request));
  }
}
