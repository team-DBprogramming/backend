package com.example.backend.apiPayload;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.backend.apiPayload.code.ErrorReasonDTO;
import com.example.backend.apiPayload.code.ReasonDTO;
import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.apiPayload.exception.GeneralException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiPayloadTest {

  @Test
  void createsSuccessResponseWithDefaultStatus() {
    ApiResponse<String> response = ApiResponse.onSuccess("payload");

    assertThat(response.getIsSuccess()).isTrue();
    assertThat(response.getCode()).isEqualTo("COMMON200");
    assertThat(response.getMessage()).isEqualTo("요청에 성공했습니다.");
    assertThat(response.getResult()).isEqualTo("payload");
  }

  @Test
  void createsSuccessResponseFromCustomStatus() {
    ApiResponse<String> response = ApiResponse.of(SuccessStatus._CREATED, "created");

    assertThat(response.getIsSuccess()).isTrue();
    assertThat(response.getCode()).isEqualTo("COMMON201");
    assertThat(response.getMessage()).isEqualTo("요청이 성공적으로 생성되었습니다.");
    assertThat(response.getResult()).isEqualTo("created");
  }

  @Test
  void createsFailureResponse() {
    ApiResponse<String> response = ApiResponse.onFailure("COMMON400", "잘못된 요청입니다.", "detail");

    assertThat(response.getIsSuccess()).isFalse();
    assertThat(response.getCode()).isEqualTo("COMMON400");
    assertThat(response.getMessage()).isEqualTo("잘못된 요청입니다.");
    assertThat(response.getResult()).isEqualTo("detail");
  }

  @Test
  void exposesHttpStatusReasonFromStatuses() {
    ReasonDTO successReason = SuccessStatus._OK.getReasonHttpStatus();
    ErrorReasonDTO errorReason = ErrorStatus._BAD_REQUEST.getReasonHttpStatus();

    assertThat(successReason.getHttpStatus()).isEqualTo(HttpStatus.OK);
    assertThat(errorReason.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void createsGeneralExceptionFromErrorCode() {
    GeneralException exception = new GeneralException(ErrorStatus._FORBIDDEN);

    assertThat(exception.getMessage()).isEqualTo("금지된 요청입니다.");
    assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("COMMON403");
    assertThat(exception.getErrorReasonHttpStatus().getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
