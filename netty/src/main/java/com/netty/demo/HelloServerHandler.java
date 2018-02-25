package com.netty.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;

/**
 * Created by sunfucheng on 2017/7/2.
 */
public class HelloServerHandler extends SimpleChannelInboundHandler<String>{


    /**
     * 处理消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        /** 收到消息直接打印输出*/
        System.out.println("client say : "+msg);
        ctx.writeAndFlush("I have receive you message...");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() +" active!!!");
        ctx.writeAndFlush("Wellcome to "+ InetAddress.getLocalHost().getHostName()+" service!");
        super.channelActive(ctx);
    }
}
