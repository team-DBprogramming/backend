package com.example.backend.apiPayload.code.status;

import com.example.backend.apiPayload.code.BaseErrorCode;
import com.example.backend.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

  TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "테스트용 예외입니다."),
  _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러가 발생했습니다."),
  _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
  _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
  _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
  AUTH_MISSING_CREDENTIALS(HttpStatus.BAD_REQUEST, "AUTH4001", "아이디와 비밀번호를 입력해주세요."),
  AUTH_MISSING_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4002", "Refresh Token을 입력해주세요."),
  AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4011", "아이디 또는 비밀번호가 올바르지 않습니다."),
  AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4012", "유효하지 않은 토큰입니다."),
  AUTH_MISSING_ROLE(HttpStatus.UNAUTHORIZED, "AUTH4013", "아이디 또는 비밀번호가 올바르지 않습니다."),
  PROFESSOR_REQUEST_ALREADY_PROCESSED(
      HttpStatus.BAD_REQUEST, "PROFESSOR4001", "이미 승인 또는 거절 처리가 완료된 수강 요청입니다."),
  PROFESSOR_REQUEST_INVALID_STATUS(
      HttpStatus.BAD_REQUEST, "PROFESSOR4002", "수강 요청 상태는 APPROVED 또는 REJECTED만 가능합니다."),
  PROFESSOR_EXPORT_INVALID_FORMAT(
      HttpStatus.BAD_REQUEST, "PROFESSOR4003", "다운로드 형식은 xlsx 또는 csv만 가능합니다."),
  PROFESSOR_DIVISION_REQUIRED(HttpStatus.BAD_REQUEST, "PROFESSOR4005", "분반을 입력해주세요."),
  PROFESSOR_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFESSOR4041", "수강 요청을 찾을 수 없습니다."),
  PROFESSOR_FORBIDDEN(HttpStatus.FORBIDDEN, "PROFESSOR4031", "교수 권한이 필요합니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ErrorReasonDTO getReason() {
    return ErrorReasonDTO.builder().message(message).code(code).isSuccess(false).build();
  }

  @Override
  public ErrorReasonDTO getReasonHttpStatus() {
    return ErrorReasonDTO.builder()
        .message(message)
        .code(code)
        .isSuccess(false)
        .httpStatus(httpStatus)
        .build();
  }
}
