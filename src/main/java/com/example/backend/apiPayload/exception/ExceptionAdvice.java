package com.example.backend.apiPayload.exception;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.ErrorReasonDTO;
import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
    String errorStatusName =
        e.getConstraintViolations().stream()
            .map(constraintViolation -> constraintViolation.getMessage())
            .findFirst()
            .orElse(ErrorStatus._BAD_REQUEST.name());

    return handleExceptionInternalConstraint(
        e, resolveErrorStatus(errorStatusName), HttpHeaders.EMPTY, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    Map<String, String> errors = new LinkedHashMap<>();
    String errorField = "request_body";
    String errorMessage = ErrorStatus._BAD_REQUEST.getMessage();

    if (e.getCause() instanceof UnrecognizedPropertyException unrecognizedPropertyException) {
      errorField = unrecognizedPropertyException.getPropertyName();
      errorMessage = "올바른 필드를 입력해주세요.";
    }

    errors.put(errorField, errorMessage);
    return handleExceptionInternalArgs(e, HttpHeaders.EMPTY, ErrorStatus._BAD_REQUEST, request, errors);
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    Map<String, String> errors = new LinkedHashMap<>();

    e.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError -> {
              String fieldName = fieldError.getField();
              String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
              errors.merge(
                  fieldName,
                  errorMessage,
                  (existingErrorMessage, newErrorMessage) ->
                      existingErrorMessage + ", " + newErrorMessage);
            });

    return handleExceptionInternalArgs(e, HttpHeaders.EMPTY, ErrorStatus._BAD_REQUEST, request, errors);
  }

  @ExceptionHandler(value = GeneralException.class)
  public ResponseEntity<Object> onThrowException(
      GeneralException generalException, HttpServletRequest request) {
    ErrorReasonDTO errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
    return handleExceptionInternal(generalException, errorReasonHttpStatus, null, request);
  }

  @ExceptionHandler
  public ResponseEntity<Object> dataAccessException(DataAccessException e, WebRequest request) {
    if (containsAnyMessage(e, "ORA-20041", "ORA-20042", "NOTIFICATION_NOT_FOUND")) {
      return handleExceptionInternalConstraint(
          e, ErrorStatus.NOTIFICATION_NOT_FOUND, HttpHeaders.EMPTY, request);
    }
    return handleExceptionInternalFalse(
        e,
        ErrorStatus._INTERNAL_SERVER_ERROR,
        HttpHeaders.EMPTY,
        ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus(),
        request,
        ErrorStatus._INTERNAL_SERVER_ERROR.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Object> exception(Exception e, WebRequest request) {
    log.error("처리되지 않은 예외가 발생했습니다.", e);

    return handleExceptionInternalFalse(
        e,
        ErrorStatus._INTERNAL_SERVER_ERROR,
        HttpHeaders.EMPTY,
        ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus(),
        request,
        ErrorStatus._INTERNAL_SERVER_ERROR.getMessage());
  }

  private ResponseEntity<Object> handleExceptionInternal(
      Exception e, ErrorReasonDTO reason, HttpHeaders headers, HttpServletRequest request) {

    ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null);
    WebRequest webRequest = new ServletWebRequest(request);
    return super.handleExceptionInternal(e, body, headers, reason.getHttpStatus(), webRequest);
  }

  private ResponseEntity<Object> handleExceptionInternalFalse(
      Exception e,
      ErrorStatus errorCommonStatus,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request,
      String errorPoint) {
    ApiResponse<Object> body =
        ApiResponse.onFailure(
            errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorPoint);
    return super.handleExceptionInternal(e, body, headers, status, request);
  }

  private ResponseEntity<Object> handleExceptionInternalArgs(
      Exception e,
      HttpHeaders headers,
      ErrorStatus errorCommonStatus,
      WebRequest request,
      Map<String, String> errorArgs) {
    ApiResponse<Object> body =
        ApiResponse.onFailure(
            errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorArgs);
    return super.handleExceptionInternal(
        e, body, headers, errorCommonStatus.getHttpStatus(), request);
  }

  private ResponseEntity<Object> handleExceptionInternalConstraint(
      Exception e, ErrorStatus errorCommonStatus, HttpHeaders headers, WebRequest request) {
    ApiResponse<Object> body =
        ApiResponse.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), null);
    return super.handleExceptionInternal(
        e, body, headers, errorCommonStatus.getHttpStatus(), request);
  }

  private ErrorStatus resolveErrorStatus(String name) {
    try {
      return ErrorStatus.valueOf(name);
    } catch (IllegalArgumentException e) {
      return ErrorStatus._BAD_REQUEST;
    }
  }

  private boolean containsAnyMessage(Throwable throwable, String... needles) {
    Throwable current = throwable;
    while (current != null) {
      String message = current.getMessage();
      if (message != null) {
        for (String needle : needles) {
          if (message.contains(needle)) {
            return true;
          }
        }
      }
      current = current.getCause();
    }
    return false;
  }
}
