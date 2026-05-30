package com.example.backend.mapper;

import com.example.backend.dto.auth.AuthUser;
import java.time.Instant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {

  AuthUser findByLoginId(@Param("loginId") String loginId);

  void updateLastLoginAt(@Param("userId") Long userId, @Param("lastLoginAt") Instant lastLoginAt);
}
