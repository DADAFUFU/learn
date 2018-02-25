package com.netty.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by sunfucheng on 2017/7/2.
 */
public class HelloClientHandler extends SimpleChannelInboundHandler<String> {

    /**
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        /**接受服务器的消息*/
        System.out.println("Server say:"+msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client active ...");
        super.channelActive(ctx);
    }
}
