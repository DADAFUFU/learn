package com.dubbo.demo.service.impl;

import com.dubbo.demo.service.DemoService;

/**
 * Created by dafu on 2018/2/26.
 */
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        return "hello "+name;
    }
}
