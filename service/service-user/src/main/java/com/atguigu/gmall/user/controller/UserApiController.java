package com.atguigu.gmall.user.controller;


import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user/passport")
public class UserApiController {

    @Autowired
    UserService userService;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("login")
    Result login(HttpServletRequest request, @RequestBody UserInfo userInfo){
        Map<String,String> tokenMap = userService.login(userInfo);
        String token = tokenMap.get("token");
        if(StringUtils.isEmpty(token)){
            return Result.ok(ResultCodeEnum.LOGIN_AUTH);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", userInfo.getName());
        map.put("nickName", userInfo.getNickName());
        map.put("token", token);

        // 调用购物车的feign合并接口
        String userId = tokenMap.get("userId");
        String userTempId = AuthContextHolder.getUserTempId(request);

        // 调用购物车合并功能
        boolean b = cartFeignClient.checkIfMergeToCartList(userTempId);
        if(b){
            cartFeignClient.mergeToCartList(userId,userTempId);
        }
        return Result.ok(map);

    }


    @RequestMapping("inner/getUserId/{token}")
    String getUserId(@PathVariable("token") String token){

        String userId = userService.getUserId(token);

        return userId;
    }


//    @RequestMapping("logout")
//    public Result logout(HttpServletRequest request) {
//        redisTemplate.delete(RedisConst.USER_LOGIN_KEY_PREFIX + request.getHeader("token"));
//        return Result.ok();
//    }
}
