package com.example.backend.apiPayload.exception.handler;

import com.example.backend.apiPayload.code.BaseErrorCode;
import com.example.backend.apiPayload.exception.GeneralException;

public class ProfessorHandler extends GeneralException {

  public ProfessorHandler(BaseErrorCode code) {
    super(code);
  }
}
