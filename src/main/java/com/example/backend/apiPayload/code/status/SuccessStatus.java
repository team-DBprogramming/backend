package com.example.backend.apiPayload.code.status;

import com.example.backend.apiPayload.code.BaseCode;
import com.example.backend.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

  _OK(HttpStatus.OK, "COMMON200", "요청에 성공했습니다."),
  _CREATED(HttpStatus.CREATED, "COMMON201", "요청이 성공적으로 생성되었습니다."),
  AUTH_LOGIN(HttpStatus.OK, "AUTH200", "로그인 성공"),
  AUTH_LOGOUT(HttpStatus.OK, "AUTH200", "로그아웃 성공"),
  AUTH_REISSUE(HttpStatus.OK, "AUTH200", "Access Token 재발급 성공"),
  NOTIFICATION_LIST(HttpStatus.OK, "NOTIFICATION200", "알림 목록 조회 성공"),
  NOTIFICATION_DETAIL(HttpStatus.OK, "NOTIFICATION200", "알림 상세 조회 성공"),
  NOTIFICATION_READ(HttpStatus.OK, "NOTIFICATION200", "알림 읽음 처리 성공"),
  PROFESSOR_COURSES(HttpStatus.OK, "PROFESSOR200", "담당 강의 목록 조회 성공"),
  PROFESSOR_DASHBOARD(HttpStatus.OK, "PROFESSOR200", "교수 대시보드 조회 성공"),
  PROFESSOR_REQUESTS(HttpStatus.OK, "PROFESSOR200", "수강 요청 조회 성공"),
  PROFESSOR_STUDENTS(HttpStatus.OK, "PROFESSOR200", "수강생 목록 조회 성공"),
  PROFESSOR_REVIEWS(HttpStatus.OK, "PROFESSOR200", "강의 평가 조회 성공"),
  PROFESSOR_MESSAGE_SENT(HttpStatus.CREATED, "PROFESSOR201", "메시지를 전송했습니다."),
  PROFESSOR_REQUEST_PROCESSED(HttpStatus.CREATED, "PROFESSOR201", "수강 요청 처리가 완료되었습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ReasonDTO getReason() {
    return ReasonDTO.builder().message(message).code(code).isSuccess(true).build();
  }

  @Override
  public ReasonDTO getReasonHttpStatus() {
    return ReasonDTO.builder()
        .message(message)
        .code(code)
        .isSuccess(true)
        .httpStatus(httpStatus)
        .build();
  }
}
