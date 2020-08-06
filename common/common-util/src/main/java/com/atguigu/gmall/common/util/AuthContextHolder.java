package com.atguigu.gmall.common.util;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class AuthContextHolder {
    public static String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        return StringUtils.isEmpty(userId)? "" : userId;

    }


    public static String getUserTempId(HttpServletRequest request) {
        String userTempId = request.getHeader("userTempId");

        return StringUtils.isEmpty(userTempId)? "" : userTempId;
    }
}
