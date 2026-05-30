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
  AUTH_LOGOUT(HttpStatus.OK, "AUTH200", "로그아웃 성공");

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
