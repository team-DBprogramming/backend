package com.example.backend.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {

  void callAuthenticateLogin(Map<String, Object> params);
}
