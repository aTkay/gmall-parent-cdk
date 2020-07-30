package com.atguigu.gmall.list.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListApiService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/list")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    ListApiService listApiService;

    @RequestMapping("createIndex")
    public Result createIndex(){
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    @RequestMapping("inner/upperGoods/{skuId}")
    Result upperGoods(@PathVariable("skuId") Long skuId){
        listApiService.upperGoods(skuId);
        return Result.ok();
    }

    @RequestMapping("inner/lowerGoods/{skuId}")
    Result lowerGoods(@PathVariable("skuId") Long skuId){
        listApiService.lowerGoods(skuId);
        return Result.ok();
    }

    @RequestMapping("inner/incrHotScore/{skuId}")
     void incrHotScore(@PathVariable("skuId") Long skuId){
        listApiService.incrHotScore(skuId);
    }


}
