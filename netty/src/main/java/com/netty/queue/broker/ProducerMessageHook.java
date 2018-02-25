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
package com.netty.queue.broker;

import com.google.common.base.Joiner;
import com.netty.queue.consumer.ConsumerClusters;
import com.netty.queue.consumer.ConsumerContext;
import com.netty.queue.core.*;
import com.netty.queue.model.MessageDispatchTask;
import com.netty.queue.msg.Message;
import com.netty.queue.msg.ProducerAckMessage;
import io.netty.channel.Channel;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AnyPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DFU
 */
public class ProducerMessageHook implements ProducerMessageListener {

    // 过滤一些没有用的Consumer
    private List<ConsumerClusters> clustersSet = new ArrayList<ConsumerClusters>();
    // 订阅过该topic的所有Consumer
    private List<ConsumerClusters> focusTopicGroup = null;

    private void filterByTopic(final String topic) {
        Predicate focusAllPredicate = new Predicate() {
            public boolean evaluate(Object object) {
                ConsumerClusters clusters = (ConsumerClusters) object;
                return clusters.findSubscriptionData(topic) != null;
            }
        };

        AnyPredicate any = new AnyPredicate(new Predicate[]{focusAllPredicate});

        Closure joinClosure = new Closure() {
            public void execute(Object input) {
                if (input instanceof ConsumerClusters) {
                    ConsumerClusters clusters = (ConsumerClusters) input;
                    // 将符合条件的放在clustersSet中
                    clustersSet.add(clusters);
                }
            }
        };

        Closure ignoreClosure = new Closure() {
            public void execute(Object input) {
            }
        };

        Closure ifClosure = ClosureUtils.ifClosure(any, joinClosure, ignoreClosure);

        CollectionUtils.forAllDo(focusTopicGroup, ifClosure);
    }

    private boolean checkClustersSet(Message msg, String requestId) {
        if (clustersSet.size() == 0) {
            System.out.println("AvatarMQ don't have match clusters!");
            ProducerAckMessage ack = new ProducerAckMessage();
            ack.setMsgId(msg.getMsgId());
            ack.setAck(requestId);
            ack.setStatus(ProducerAckMessage.SUCCESS);
            // 消费者没有消费，返回信息给生产者，消息生产成功
            AckTaskQueue.pushAck(ack);
            SemaphoreCache.release(MessageSystemConfig.AckTaskSemaphoreValue);
            return false;
        } else {
            return true;
        }
    }

    private void dispatchTask(Message msg, String topic) {
        List<MessageDispatchTask> tasks = new ArrayList<MessageDispatchTask>();

        for (int i = 0; i < clustersSet.size(); i++) {
            MessageDispatchTask task = new MessageDispatchTask();
            task.setClusters(clustersSet.get(i).getClustersId());
            task.setTopic(topic);
            task.setMessage(msg);
            tasks.add(task);

        }

        MessageTaskQueue.getInstance().pushTask(tasks);

        for (int i = 0; i < tasks.size(); i++) {
            SemaphoreCache.release(MessageSystemConfig.NotifyTaskSemaphoreValue);
        }
    }

    private void taskAck(Message msg, String requestId) {
        try {
            Joiner joiner = Joiner.on(MessageSystemConfig.MessageDelimiter).skipNulls();
            String key = joiner.join(requestId, msg.getMsgId());
            AckMessageCache.getAckMessageCache().appendMessage(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hookProducerMessage(Message msg, String requestId, Channel channel) {

        ChannelCache.pushRequest(requestId, channel);

        String topic = msg.getTopic();

        /**
         * 查找订阅例该topic的消费者
         */
        focusTopicGroup = ConsumerContext.selectByTopic(topic);

        // 根据topic过滤一些没用的Consumer
        filterByTopic(topic);

        if (checkClustersSet(msg, requestId)) {
            dispatchTask(msg, topic);
            /**
             * 通知生产者，消息转发完毕
             * 类似checkClustersSet方法，需要释放锁，要不然不能继续运行。
             */
            taskAck(msg, requestId);
            clustersSet.clear();
        } else {
            return;
        }
    }
}
