package com.example.backend.apiPayload.exception.handler;

import com.example.backend.apiPayload.code.BaseErrorCode;
import com.example.backend.apiPayload.exception.GeneralException;

public class StudentHandler extends GeneralException {

  public StudentHandler(BaseErrorCode code) {
    super(code);
  }
}
