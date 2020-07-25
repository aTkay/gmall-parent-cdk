package com.atguigu.gmall.test.juc8;

public class Test {
    public static void main(String[] args) {

        MyPhone myPhone = new MyPhone();
        MyPhone myPhone1 = new MyPhone();

        new Thread(()->{
            myPhone.senMsg();
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //执行正在打电话运行了2秒，睡眠1秒+自己一秒
        new Thread(()->{
            myPhone.call();
        }).start();

        //1  同一个对象的两个syn会阻塞 5秒


    }
}
