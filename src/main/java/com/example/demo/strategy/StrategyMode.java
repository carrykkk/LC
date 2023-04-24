package com.example.demo.strategy;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

public class StrategyMode {

    enum ShareType {
        SINGLE("single", "单商品"),
        MULTI("multi", "多商品"),
        ORDER("order", "下单");
        // 场景对应的编码
        private String code;

        // 业务场景描述
        private String desc;

        ShareType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }
        // 省略 get set 方法
    }

    private static HashMap<String, A> map = new HashMap<>();
    static {
        map.put("single",new oneStategy());
    }

    public static A getStategy(String type) {
        if (type == null || StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("");
        }
        A a = map.get(type);
        return a;
    }

    public static void main() {
        String type = "ddd";
        A stategy = StrategyMode.getStategy(type);
        stategy.doSomething();

    }

}
