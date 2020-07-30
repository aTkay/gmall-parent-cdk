package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.list.service.ListApiService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListApiServiceImpl implements ListApiService {

    @Autowired
    GoodsRepository elasticsearchRepository;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RestHighLevelClient restHighLevelClient;

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

    @Override
    public SearchResponseVo list(@RequestBody SearchParam searchParam) {
        //拼接dsl的封装
        SearchRequest searchRequest = buildQueryDsl(searchParam);
        //执行dsl查询命令
        SearchResponse search =null;
        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //封装对象vo
        SearchResponseVo searchResponseVo = parseSearchResult(search);
        return searchResponseVo;
    }

    private SearchResponseVo parseSearchResult(SearchResponse search) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //解析返回结果
        //商品

        //返回searchResponseVo.setGoodsList(goods);
        List<Goods> goods = new ArrayList<>();
        SearchHits hits = search.getHits();
        SearchHit[] resultHits = hits.getHits();
        if (null != resultHits && resultHits.length>0){
            for (SearchHit resultHit : resultHits) {
                String sourceAsString = resultHit.getSourceAsString();
                //转化JSON对象
                Goods good = JSON.parseObject(sourceAsString, Goods.class);
                goods.add(good);
            }
        }
        List<SearchResponseTmVo> trademarkList = new ArrayList<>();
        HashSet<SearchResponseTmVo> set = new HashSet<>();
        for (Goods good : goods) {
            Long tmId = good.getTmId();
            String tmName = good.getTmName();
            String tmLogoUrl = good.getTmLogoUrl();
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            searchResponseTmVo.setTmName(tmName);
            searchResponseTmVo.setTmId(tmId);
            set.add(searchResponseTmVo);
        }
        for (SearchResponseTmVo searchResponseTmVo : set) {
            trademarkList.add(searchResponseTmVo);
        }

//        //商品聚合解析
//        Map<String, Aggregation> stringAggregationMap = search.getAggregations().asMap();
//        ParsedLongTerms tmIdAggParsedLongTerms = (ParsedLongTerms)stringAggregationMap.get("tmIdAgg");
//
//
//        //返回searchResponseVo.setTrademarkList(trademarkList);
//
//        List<SearchResponseTmVo> trademarkList = tmIdAggParsedLongTerms.getBuckets().stream().map(bucket->{
//            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
//            String keyAsString = bucket.getKeyAsString();
//
//
//            //跟解析tmId的聚合一样，在进行一次聚合循环，拿到tmName
//            Map<String, Aggregation> tmIdSubMap = bucket.getAggregations().asMap();
//            ParsedStringTerms tmNameAgg = (ParsedStringTerms)tmIdSubMap.get("tmNameAgg");
//            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
//
//            //跟解析tmId的聚合一样，在惊醒一次聚合寻黄，拿到tmLogoUrl
//            Map<String, Aggregation> tmLogoUrlSubMap = bucket.getAggregations().asMap();
//            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) tmLogoUrlSubMap.get("tmLogoUrlAgg");
//
//            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
//
//            searchResponseTmVo.setTmId(Long.parseLong(keyAsString));
//            searchResponseTmVo.setTmName(tmName);
//            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
//
//        return searchResponseTmVo;
//
//        }).collect(Collectors.toList());

        searchResponseVo.setGoodsList(goods);
        searchResponseVo.setTrademarkList(trademarkList);

        return searchResponseVo;
    }

    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        String[] indeces ={"goods"};

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //分类
        Long category1Id = searchParam.getCategory1Id();
        Long category2Id = searchParam.getCategory2Id();
        Long category13Id = searchParam.getCategory3Id();

        //if判断 3级分类是否为空
        String keyword = searchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //属性聚合
        String[] props = searchParam.getProps();

        //商标
        String trademark = searchParam.getTrademark();
        if (StringUtils.isNotBlank(trademark)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("tmId", trademark);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        //聚合商标结果
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        //分页
        //排序
        //高亮

        SearchRequest searchRequest = new SearchRequest(indeces, searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        return searchRequest;
    }


}
