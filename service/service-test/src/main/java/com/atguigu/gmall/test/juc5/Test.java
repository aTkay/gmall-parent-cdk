package com.atguigu.gmall.test.juc5;

public class Test {

    public static void main(String[] args) {

        Restaurant restaurant = new Restaurant();


        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                restaurant.cut();
            }
        },"切菜师傅").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                restaurant.cook();
            }
        },"炒菜师傅").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                restaurant.give();
                System.out.println("第"+(i+1)+"道菜完成了~~");
            }
        },"端菜师傅").start();
    }
}
