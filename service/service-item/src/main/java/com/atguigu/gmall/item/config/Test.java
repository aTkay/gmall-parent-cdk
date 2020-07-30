package com.atguigu.gmall.item.config;


import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<Object> objects = new ArrayBlockingQueue<>(3);
        objects.add("a");
        objects.add("b");
        objects.add("c");
//        objects.put("d");

        //使用offer
//        boolean e = objects.offer("e");
//        System.out.println(e);


        System.out.println(objects.element());

        Thread.sleep(2000);
//        //remove a
        objects.remove();
//
//        //查看
//        //4个运行报错
//
        System.out.println(objects.element());

    }
}
