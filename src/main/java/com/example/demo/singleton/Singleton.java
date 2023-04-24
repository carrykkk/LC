package com.example.demo.singleton;


public class Singleton {
    // 饿汉式  双重校验锁
    private static volatile Singleton instance = null;

    private Singleton(){}

    private static Singleton getInstance(){
        if (instance == null) {
            synchronized (Singleton.class){
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
