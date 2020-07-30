package com.atguigu.gmall.list.service;

public interface ListApiService {
    void upperGoods(Long skuId);

    void lowerGoods(Long skuId);

    void incrHotScore(Long skuId);
}
