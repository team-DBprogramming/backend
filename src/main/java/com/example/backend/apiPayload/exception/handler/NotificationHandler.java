package com.example.backend.apiPayload.exception.handler;

import com.example.backend.apiPayload.code.BaseErrorCode;
import com.example.backend.apiPayload.exception.GeneralException;

public class NotificationHandler extends GeneralException {

  public NotificationHandler(BaseErrorCode code) {
    super(code);
  }
}
