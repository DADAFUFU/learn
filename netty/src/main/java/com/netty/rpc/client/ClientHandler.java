package com.netty.rpc.client;

import com.netty.rpc.serializer.MethodInvoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	private Object response;
	private MethodInvoker methodInvoker;

	public ClientHandler(MethodInvoker methodInvoker) {

		this.methodInvoker = methodInvoker;

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
        
		ctx.writeAndFlush(this.methodInvoker); // 发送到下一个Handler。input处理完，就输出，然后output。
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		System.out.println("channelRead:"+msg);
		response = msg;
		ctx.close();
	}

	public Object getResponse() {
		return response;
	}

}
