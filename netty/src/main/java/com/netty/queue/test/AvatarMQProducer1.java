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

import com.netty.queue.msg.Message;
import com.netty.queue.msg.ProducerAckMessage;
import com.netty.queue.producer.AvatarMQProducer;

import java.util.Scanner;

/**
 *
 */
public class AvatarMQProducer1 {

    public static void main(String[] args) throws InterruptedException {
        AvatarMQProducer producer = new AvatarMQProducer("127.0.0.1:18888", "AvatarMQ-Topic-1");
        producer.setClusterId("AvatarMQCluster");
        producer.init();
        producer.start();
        Scanner scanner = new Scanner(System.in);
//        System.out.println(StringUtils.center("AvatarMQProducer1 消息发送开始", 50, "*"));

//        for (int i = 0; i < 10; i++) {
        while(true) {

            Message message = new Message();
            String str = scanner.nextLine();
            if(str.equals("end") || str.equals("exit")){
                producer.shutdown();
            }
            message.setBody(str.getBytes());
            ProducerAckMessage result = producer.delivery(message);
//            if (result.getStatus() == (ProducerAckMessage.SUCCESS)) {
//                System.out.printf("AvatarMQProducer1 发送消息编号:%s\n", result.getMsgId());
//            }

            Thread.sleep(100);
        }
//        }


//        System.out.println(StringUtils.center("AvatarMQProducer1 消息发送完毕", 50, "*"));
    }
}
