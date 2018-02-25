package com.netty.rpc.server.impl;


import com.netty.rpc.service.IDemoService;

/**
 * Hello world!
 *
 */
public class DemoServiceImpl implements IDemoService
{

	public int sum(int a, int b) {
		return a+b;
	}

	@Override
	public String say() {
		return "hello";
	}

}
