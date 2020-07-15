package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.model.product.BaseCategory1;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @RequestMapping("getBaseCategory1")
    public String getBaseCategory1(){
        return "hello";
    }

}
