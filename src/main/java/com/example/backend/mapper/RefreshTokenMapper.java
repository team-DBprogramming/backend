package com.example.backend.mapper;

import java.time.Instant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

  void insert(
      @Param("userId") Long userId,
      @Param("tokenHash") String tokenHash,
      @Param("rememberMe") int rememberMe,
      @Param("expiresAt") Instant expiresAt);

  int countActive(@Param("tokenHash") String tokenHash, @Param("now") Instant now);

  void revoke(@Param("tokenHash") String tokenHash, @Param("revokedAt") Instant revokedAt);
}
