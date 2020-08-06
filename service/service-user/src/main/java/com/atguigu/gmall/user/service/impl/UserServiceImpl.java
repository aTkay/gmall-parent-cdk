package com.atguigu.gmall.user.service.impl;


import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public String getUserId(String token) {

        // 从缓存中根据token去除userId
        String userId = redisTemplate.opsForValue().get("user:token:" + token)+"";

        return userId;
    }

    @Override
    public Map<String,String> login(UserInfo userInfo) {

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("login_name",userInfo.getLoginName());
        queryWrapper.eq("passwd",  DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes()));

        UserInfo userInfoReturn = userInfoMapper.selectOne(queryWrapper);

        if(null!=userInfoReturn){
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("user:token:" + token,userInfoReturn.getId());

            Map<String,String> map = new HashMap<>();
            map.put("userId",userInfoReturn.getId()+"");
            map.put("token",token);
            return map;
        }

        return null;
    }
}
