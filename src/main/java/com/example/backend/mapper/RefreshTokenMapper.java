package com.example.backend.mapper;

import java.time.Instant;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

  void callSaveLoginSuccess(Map<String, Object> params);

  void callRevokeRefreshToken(Map<String, Object> params);

  int countActive(@Param("tokenHash") String tokenHash, @Param("now") Instant now);
}
