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

import com.google.common.base.Splitter;
import com.netty.queue.msg.ProducerAckMessage;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class AckMessageTask implements Callable<Long> {

    CyclicBarrier barrier = null;
    String[] messages = null;
    private final AtomicLong count = new AtomicLong(0);

    public AckMessageTask(CyclicBarrier barrier, String[] messages) {
        this.barrier = barrier;
        this.messages = messages;
    }

    public Long call() throws Exception {
        for (int i = 0; i < messages.length; i++) {
            boolean error = false;
            ProducerAckMessage ack = new ProducerAckMessage();
            Object[] msg = Splitter.on(MessageSystemConfig.MessageDelimiter).trimResults().splitToList(messages[i]).toArray();
            if (msg.length == 2) {
                ack.setAck((String) msg[0]);
                ack.setMsgId((String) msg[1]);

                if (error) {
                    ack.setStatus(ProducerAckMessage.FAIL);
                } else {
                    ack.setStatus(ProducerAckMessage.SUCCESS);
                    count.incrementAndGet();
                }

                AckTaskQueue.pushAck(ack);
                SemaphoreCache.release(MessageSystemConfig.AckTaskSemaphoreValue);
            }
        }

        barrier.await();
        return count.get();
    }
}
