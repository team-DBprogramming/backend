package com.example.backend.dto.student;

public record StudentApiResponse<T>(boolean success, String code, String message, T data) {

  public static <T> StudentApiResponse<T> success(String code, String message, T data) {
    return new StudentApiResponse<>(true, code, message, data);
  }
}
