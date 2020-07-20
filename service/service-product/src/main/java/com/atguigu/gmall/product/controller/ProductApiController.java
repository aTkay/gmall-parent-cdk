package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.CategoryService;
import com.atguigu.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    SkuService skuService;

    @RequestMapping("inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = skuService.getSkuInfo(skuId);
        return skuInfo;
    }

    @RequestMapping("inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView baseCategoryView = categoryService.getCategoryView(category3Id);
        return baseCategoryView;
    }


}
