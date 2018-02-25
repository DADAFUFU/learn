package com.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by sunfucheng on 2017/7/2.
 */
public class HelloServer {

    private static int port = 7878;
    public static void main(String[] args){
        /**两个EventLoogGroup parentGroup一般用来接收accpt请求，childGroup用来处理各个连接的请求*/
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup,childGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decoder",new StringDecoder());
                    ch.pipeline().addLast("encoder",new StringEncoder());
                    ch.pipeline().addLast(new HelloServerHandler());
                }
            });
            /**绑定端口，开始接受进来的连接*/
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Server start listen at " + port);
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
