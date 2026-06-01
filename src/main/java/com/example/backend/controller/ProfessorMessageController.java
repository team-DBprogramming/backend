package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.dto.professor.ProfessorMessageRequest;
import com.example.backend.dto.professor.ProfessorMessageResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ProfessorMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professors/me/messages")
@Tag(name = "Professor Messages", description = "교수 학생 메시지 전송 API")
public class ProfessorMessageController {

  private final ProfessorMessageService messageService;

  public ProfessorMessageController(ProfessorMessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "학생 메시지 전송", description = "담당 강의 수강생에게 메시지를 전송하고 알림을 생성합니다.")
  public ApiResponse<ProfessorMessageResponse> sendMessage(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody ProfessorMessageRequest request) {
    return ApiResponse.of(
        SuccessStatus.PROFESSOR_MESSAGE_SENT,
        messageService.sendMessage(userDetails.toAuthenticatedUser(), request));
  }
}
