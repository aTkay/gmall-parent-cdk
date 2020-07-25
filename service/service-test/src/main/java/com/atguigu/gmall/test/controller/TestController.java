package com.atguigu.gmall.test.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {
    @Autowired
    RedisTemplate redisTemplate;


    @RequestMapping("testLockNx")
    public String testLockNx() {
        System.out.println("正在请求分布式中的一个节点微服务~~");


        String uid = UUID.randomUUID().toString();
        //分布式锁过期时间为3秒
        Boolean stockLock = redisTemplate.opsForValue().setIfAbsent("stockLock", uid, 3, TimeUnit.SECONDS);

        //stockLocck锁
        if (stockLock) {
            String stock = redisTemplate.opsForValue().get("stock").toString();

            int i = Integer.parseInt(stock);
            if (i > 0) {
                i--;
                redisTemplate.opsForValue().set("stock", i);
                System.out.println("目前库存剩余数量:" + i);
            } else {
                System.out.println("目前库存剩余数量:0");
            }

            //删除所锁之前判断一下del删除的是否是自己当前的锁
            String delUid = redisTemplate.opsForValue().get("stockLock").toString();
            if (stockLock) {
                //操作完成，释放分布式锁
                redisTemplate.delete("stockLock");
            }

            //Lua脚本防止误删
            String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

            //设置lua脚本返回的数据类型
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

            //设置脚本返回类型为Long
            redisScript.setScriptText(script);
            redisTemplate.execute(redisScript, Arrays.asList("stockLock"),uid);

        }else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return testLockNx();
        }
        return "剩余库存数量:0";
    }




    @RequestMapping("testLock")
    public String testLock() {
        System.out.println("正在请求分布式中的一个节点微服务");

        String stock = redisTemplate.opsForValue().get("stock").toString();
        int i = Integer.parseInt(stock);
        if(i>0){
            i-- ;
            redisTemplate.opsForValue().set("stock",i);
            System.out.println("目前库存剩余数量:"+i);
        }else{
            System.out.println("目前库存剩余数量:0");
        }
        return "剩余库存数量:"+i;
    }
}
