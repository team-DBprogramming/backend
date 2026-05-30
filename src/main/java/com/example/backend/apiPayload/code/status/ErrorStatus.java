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
  AUTH_MISSING_ROLE(HttpStatus.UNAUTHORIZED, "AUTH4013", "아이디 또는 비밀번호가 올바르지 않습니다.");

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
