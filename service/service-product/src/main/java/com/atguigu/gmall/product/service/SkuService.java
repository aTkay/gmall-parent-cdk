package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);


    IPage<SkuInfo> list(Page pageParam);

    void cancelSale(Long skuId);

    void onSale(Long skuId);

    SkuInfo getSkuInfo(Long skuId);
}
