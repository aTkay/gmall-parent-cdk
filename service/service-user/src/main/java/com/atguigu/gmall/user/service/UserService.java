package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

import java.util.Map;

public interface UserService {
    String getUserId(String token);

    Map<String,String> login(UserInfo userInfo);
}
