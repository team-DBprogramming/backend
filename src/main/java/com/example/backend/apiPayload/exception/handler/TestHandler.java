package com.example.backend.apiPayload.exception.handler;

import com.example.backend.apiPayload.code.BaseErrorCode;
import com.example.backend.apiPayload.exception.GeneralException;

public class TestHandler extends GeneralException {

  public TestHandler(BaseErrorCode code) {
    super(code);
  }
}
