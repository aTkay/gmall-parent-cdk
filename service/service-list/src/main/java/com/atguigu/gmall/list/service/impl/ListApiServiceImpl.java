package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.service.ListApiService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ListApiServiceImpl implements ListApiService {

    @Autowired
    GoodsRepository elasticsearchRepository;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    static RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) throws IOException {

        // dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        //query
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title","华为");
        boolQueryBuilder.must(matchQueryBuilder);
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id",61);
        boolQueryBuilder.filter(termQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);

        System.out.println("========"+searchSourceBuilder.toString()+"============");

        // 请求命令对象得封装
        String[] indeces = {"goods"};
        SearchRequest searchRequest = new SearchRequest(indeces,searchSourceBuilder);
        //SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

    }

    @Override
    public void upperGoods(Long skuId) {

        // 查询skuInfo
        Goods goods = new Goods();

        //查询sku对应的平台属性
        List<SearchAttr> searchAttrs =  productFeignClient.getAttrList(skuId);
        goods.setAttrs(searchAttrs);

        //查询sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        // 查询品牌
        BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
        if (baseTrademark != null){
            goods.setTmId(skuInfo.getTmId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());

        }

        // 查询分类
        BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        if (baseCategoryView != null) {
            goods.setCategory1Id(baseCategoryView.getCategory1Id());
            goods.setCategory1Name(baseCategoryView.getCategory1Name());
            goods.setCategory2Id(baseCategoryView.getCategory2Id());
            goods.setCategory2Name(baseCategoryView.getCategory2Name());
            goods.setCategory3Id(baseCategoryView.getCategory3Id());
            goods.setCategory3Name(baseCategoryView.getCategory3Name());
        }

        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setId(skuInfo.getId());
        goods.setTitle(skuInfo.getSkuName());
        goods.setCreateTime(new Date());

        elasticsearchRepository.save(goods);

    }

    @Override
    public void lowerGoods(Long skuId) {

        elasticsearchRepository.deleteById(skuId);

    }

    @Override
    public void incrHotScore(Long skuId) {
        // 更新redis，返回当前分数
        Double aDouble = redisTemplate.opsForZSet().incrementScore("hotScore", skuId, 1);

        // 用当前分数摸10，如果没有余数，则更新es
        if(aDouble%10==0){
            // 调用es更新分数
            Optional<Goods> optional = elasticsearchRepository.findById(skuId);
            Goods goods = optional.get();
            goods.setHotScore(aDouble.longValue());
            elasticsearchRepository.save(goods);

        }

    }
}
