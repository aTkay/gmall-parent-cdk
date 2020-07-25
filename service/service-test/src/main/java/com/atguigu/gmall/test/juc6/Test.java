package com.atguigu.gmall.test.juc6;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {


        // 在一个方法中分出三条线程，分别查询三个商品接口


        //第一个线程
        CallableImpl callableJd = new CallableImpl("jd");
        FutureTask<Double> jDTask = new FutureTask<Double>(callableJd);
        new Thread(jDTask).start();

        //第二个线程
        CallableImpl callableTb = new CallableImpl("tb");
        FutureTask<Double> tBTask = new FutureTask<Double>(callableTb);
        new Thread(tBTask).start();

        //第三个线程
        CallableImpl callablePdd = new CallableImpl("pdd");
        FutureTask<Double> pddTask = new FutureTask<Double>(callablePdd);
        new Thread(pddTask).start();

        //取出三个线程
        Double jdPrice = jDTask.get();
        Double tbPrice = tBTask.get();
        Double pddPrice = pddTask.get();

        System.out.println("该商品京东价格为"+jdPrice);
        System.out.println("该商品淘宝价格为"+tbPrice);
        System.out.println("该商品拼多多价格为"+pddPrice);

        System.out.println("主线程执行完成~~~");

    }
}
