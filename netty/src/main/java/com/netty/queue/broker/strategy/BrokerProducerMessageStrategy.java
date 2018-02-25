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
package com.netty.queue.broker.strategy;

import com.netty.queue.broker.ConsumerMessageListener;
import com.netty.queue.broker.ProducerMessageListener;
import com.netty.queue.model.RequestMessage;
import com.netty.queue.model.ResponseMessage;
import com.netty.queue.msg.Message;
import io.netty.channel.ChannelHandlerContext;


public class BrokerProducerMessageStrategy implements BrokerStrategy {

    private ProducerMessageListener hookProducer;
    private ChannelHandlerContext channelHandler;

    public BrokerProducerMessageStrategy() {

    }

    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        Message message = (Message) request.getMsgParams();
        hookProducer.hookProducerMessage(message, request.getMsgId(), channelHandler.channel());
    }

    public void setHookProducer(ProducerMessageListener hookProducer) {
        this.hookProducer = hookProducer;
    }

    public void setChannelHandler(ChannelHandlerContext channelHandler) {
        this.channelHandler = channelHandler;
    }

    public void setHookConsumer(ConsumerMessageListener hookConsumer) {

    }
}
