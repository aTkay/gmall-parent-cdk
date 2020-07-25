package com.atguigu.gmall.test.juc6;


import java.util.concurrent.Callable;

public class CallableImpl implements Callable<Double> {

    private String mall = "";

    public CallableImpl(String mall) {
        this.mall = mall;
    }

    @Override
    public Double call() throws Exception {
        Double price = 0d;

        if (mall.equals("jd")){
            System.out.println("正在查询京东价格~~");
            price = 100d;
        }else if (mall.equals("tb")){
            System.out.println("正在查询淘宝价格~~");
            price =88d;
        }else {

            System.out.println("正在查询拼多多价格~~");
            price =60d;

        }
        return price;
    }
}

