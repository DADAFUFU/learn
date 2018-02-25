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
package com.netty.queue.core;

import com.netty.queue.msg.ProducerAckMessage;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AckTaskQueue {

    private static ConcurrentLinkedQueue<ProducerAckMessage> ackQueue = new ConcurrentLinkedQueue<ProducerAckMessage>();

    public static boolean pushAck(ProducerAckMessage ack) {
        return ackQueue.offer(ack);
    }

    public static boolean pushAck(List<ProducerAckMessage> acks) {
        boolean flag = false;
        for (ProducerAckMessage ack : acks) {
            flag = ackQueue.offer(ack);
        }
        return flag;
    }

    public static ProducerAckMessage getAck() {
        return ackQueue.poll();
    }
}
