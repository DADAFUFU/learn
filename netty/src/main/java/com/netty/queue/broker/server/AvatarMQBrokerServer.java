/**
 * Copyright (C) 2016 Newland Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netty.queue.broker.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.netty.queue.broker.ConsumerMessageHook;
import com.netty.queue.broker.MessageBrokerHandler;
import com.netty.queue.broker.ProducerMessageHook;
import com.netty.queue.core.MessageSystemConfig;
import com.netty.queue.netty.MessageObjectDecoder;
import com.netty.queue.netty.MessageObjectEncoder;
import com.netty.queue.netty.NettyClustersConfig;
import com.netty.queue.netty.NettyUtil;
import com.netty.queue.serialize.KryoCodecUtil;
import com.netty.queue.serialize.KryoPoolFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author DFU
 */
public class AvatarMQBrokerServer extends BrokerParallelServer implements RemotingServer {

    private ThreadFactory threadBossFactory = new ThreadFactoryBuilder()
            .setNameFormat("AvatarMQBroker[BossSelector]-%d")
            .setDaemon(true)
            .build();

    private ThreadFactory threadWorkerFactory = new ThreadFactoryBuilder()
            .setNameFormat("AvatarMQBroker[WorkerSelector]-%d")
            .setDaemon(true)
            .build();

    private int brokerServerPort = 0;
    private ServerBootstrap bootstrap;
    private MessageBrokerHandler handler;
    private SocketAddress serverIpAddr;
    private NettyClustersConfig nettyClustersConfig = new NettyClustersConfig();
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private EventLoopGroup boss;
    private EventLoopGroup workers;

    public AvatarMQBrokerServer(String serverAddress) {
        String[] ipAddr = serverAddress.split(MessageSystemConfig.IpV4AddressDelimiter);

        if (ipAddr.length == 2) {
            serverIpAddr = NettyUtil.string2SocketAddress(serverAddress);
        }
    }

    public void init() {
        try {
            // 创建生产者消费者监听器并实例化
            handler = new MessageBrokerHandler().buildConsumerHook(new ConsumerMessageHook()).buildProducerHook(new ProducerMessageHook());

            boss = new NioEventLoopGroup(1, threadBossFactory);

            workers = new NioEventLoopGroup(parallel, threadWorkerFactory, NettyUtil.getNioSelectorProvider());

            final KryoCodecUtil util = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());

            bootstrap = new ServerBootstrap();

            bootstrap.group(boss, workers).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_SNDBUF, nettyClustersConfig.getClientSocketSndBufSize())
                    .option(ChannelOption.SO_RCVBUF, nettyClustersConfig.getClientSocketRcvBufSize())
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .localAddress(serverIpAddr)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    defaultEventExecutorGroup,
                                    new MessageObjectEncoder(util),
                                    new MessageObjectDecoder(util),
                                    handler);
                        }
                    });

            super.init();
        } catch (IOException ex) {
            Logger.getLogger(AvatarMQBrokerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int localListenPort() {
        return brokerServerPort;
    }

    public void shutdown() {
        try {
            super.shutdown();
            boss.shutdownGracefully();
            workers.shutdownGracefully();
            defaultEventExecutorGroup.shutdownGracefully();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("AvatarMQBrokerServer shutdown exception!");
        }
    }

    public void start() {
        try {
            String ipAddress = NettyUtil.socketAddress2String(serverIpAddr);
            System.out.printf("broker server ip:[%s]\n", ipAddress);

            ChannelFuture sync = this.bootstrap.bind().sync();

            super.start();

            sync.channel().closeFuture().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            brokerServerPort = addr.getPort();
        } catch (InterruptedException ex) {
            Logger.getLogger(AvatarMQBrokerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
