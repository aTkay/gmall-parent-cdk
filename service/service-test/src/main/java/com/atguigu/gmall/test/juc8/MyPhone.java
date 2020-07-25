package com.atguigu.gmall.test.juc8;


public class MyPhone {

    //synchronized
    public   synchronized void senMsg(){
        System.out.println("正在发送短信");

        //设置睡眠时间
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //在定义一个方法,打电话
    public  void call(){

        //再设置睡眠时间
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("正在打电话~~");

    }
}
