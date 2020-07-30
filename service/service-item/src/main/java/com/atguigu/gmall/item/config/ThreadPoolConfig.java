package com.atguigu.gmall.item.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {

        //核心线程数 * 拥有最多线程数 *表示空闲线程的存活时间

        return new ThreadPoolExecutor(50, 200, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
    }
}