package com.atguigu.gmall.test.juc5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {

    private String status = "cut";

    Lock lock = new ReentrantLock();

    //声明3条线程

    Condition conditionCut = lock.newCondition();
    Condition conditionCook = lock.newCondition();
    Condition conditionGive = lock.newCondition();

    public void cut() {
        lock.lock();
        try {
            while (!status.equals("cut")) {
                try {
                    conditionCut.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(Thread.currentThread().getName() + "开始切菜咯~~");
            status = "cook";
            conditionCook.signal();
        } finally {
            lock.unlock();
        }
    }


    public void cook() {
        lock.lock();
        try {
            while (!status.equals("cook")) {
                try {
                    conditionCook.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(Thread.currentThread().getName() + "开始炒菜咯~~");
            status = "give";
            conditionGive.signal();
        } finally {
            lock.unlock();
        }
    }


    public void give() {
        lock.lock();
        try {
            while (!status.equals("give")) {
                try {
                    conditionGive.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(Thread.currentThread().getName() + "开始端菜咯~~");
            status = "cut";
            conditionCut.signal();
        } finally {
            lock.unlock();
        }
    }

}

