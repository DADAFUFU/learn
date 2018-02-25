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
package com.netty.queue.producer;

import com.netty.queue.core.AvatarMQAction;
import com.netty.queue.model.MessageSource;
import com.netty.queue.model.MessageType;
import com.netty.queue.model.RequestMessage;
import com.netty.queue.model.ResponseMessage;
import com.netty.queue.msg.Message;
import com.netty.queue.msg.ProducerAckMessage;
import com.netty.queue.netty.MessageProcessor;

import java.util.concurrent.atomic.AtomicLong;


public class AvatarMQProducer extends MessageProcessor implements AvatarMQAction {

    private boolean brokerConnect = false;
    private boolean running = false;
    private String brokerServerAddress;
    private String topic;
    private String defaultClusterId = "AvatarMQProducerClusters";
    private String clusterId = "";
    private AtomicLong msgId = new AtomicLong(0L);

    public AvatarMQProducer(String brokerServerAddress, String topic) {
        super(brokerServerAddress);
        this.brokerServerAddress = brokerServerAddress;
        this.topic = topic;
    }

    private ProducerAckMessage checkMode() {
        if (!brokerConnect) {
            ProducerAckMessage ack = new ProducerAckMessage();
            ack.setStatus(ProducerAckMessage.FAIL);
            return ack;
        }

        return null;
    }

    public void start() {
        // 链接服务器
        super.getMessageConnectFactory().connect();
        brokerConnect = true;
        running = true;
    }

    public void init() {
        //初始化操作
        ProducerHookMessageEvent hook = new ProducerHookMessageEvent();
        hook.setBrokerConnect(brokerConnect);
        hook.setRunning(running);
        super.getMessageConnectFactory().setMessageHandle(new MessageProducerHandler(this, hook));
    }

    public ProducerAckMessage delivery(Message message) {
        if (!running || !brokerConnect) {
            return checkMode();
        }

        message.setTopic(topic);
        message.setTimeStamp(System.currentTimeMillis());

        RequestMessage request = new RequestMessage();
        request.setMsgId(String.valueOf(msgId.incrementAndGet()));
        request.setMsgParams(message);
        request.setMsgType(MessageType.AvatarMQMessage);
        request.setMsgSource(MessageSource.AvatarMQProducer);
        message.setMsgId(request.getMsgId());

        ResponseMessage response = (ResponseMessage) sendAsynMessage(request);
        if (response == null) {
            ProducerAckMessage ack = new ProducerAckMessage();
            ack.setStatus(ProducerAckMessage.FAIL);
            return ack;
        }

        ProducerAckMessage result = (ProducerAckMessage) response.getMsgParams();
        return result;
    }

    public void shutdown() {
        if (running) {
            running = false;
            super.getMessageConnectFactory().close();
            super.closeMessageConnectFactory();
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
}
