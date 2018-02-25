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
package com.netty.queue.test;


import com.netty.queue.consumer.AvatarMQConsumer;
import com.netty.queue.consumer.ProducerMessageHook;
import com.netty.queue.msg.ConsumerAckMessage;
import com.netty.queue.msg.Message;

/**
 * @filename:AvatarMQConsumer1.java
 * @description:AvatarMQConsumer1功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQConsumer1 {

    private static ProducerMessageHook hook = new ProducerMessageHook() {
        public ConsumerAckMessage hookMessage(Message message) {
            System.out.println(new String(message.getBody()));
            ConsumerAckMessage result = new ConsumerAckMessage();
            result.setStatus(ConsumerAckMessage.SUCCESS);
            return result;
        }
    };

    public static void main(String[] args) {
        AvatarMQConsumer consumer = new AvatarMQConsumer("127.0.0.1:18888", "AvatarMQ-Topic-1", hook);
        consumer.init();
        consumer.setClusterId("AvatarMQCluster");
        consumer.receiveMode();
        consumer.start();
    }
}
