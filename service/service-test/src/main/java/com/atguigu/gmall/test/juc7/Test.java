package com.atguigu.gmall.test.juc7;

public class Test {
    public static void main(String[] args) {

        //RunnableImpl接口
        //第一种写法
        RunnableImpl r = new RunnableImpl();
        new Thread(r).start();


        //第二种写法  ()->{}
        new Thread(()->{

        }).start();

    }
}
