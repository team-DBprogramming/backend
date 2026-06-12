package com.example.backend.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {

  void callSaveLoginSuccess(Map<String, Object> params);

  void callRevokeRefreshToken(Map<String, Object> params);

  void callValidateRefreshTokenActive(Map<String, Object> params);
}
