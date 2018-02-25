package com.netty.rpc.client;


import com.netty.rpc.service.IDemoService;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {

		ClientInvocationHandler<IDemoService> proxy = new ClientInvocationHandler<IDemoService>();
		proxy.setClass(IDemoService.class);
		IDemoService service = proxy.get();
		System.out.println("result:" + service.say());
	}
}
