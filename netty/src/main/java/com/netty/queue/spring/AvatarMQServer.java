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
package com.netty.queue.spring;

import com.netty.queue.broker.server.AvatarMQBrokerServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AvatarMQServer extends AvatarMQBrokerServer implements ApplicationContextAware, InitializingBean {

    private String serverAddress;

    public AvatarMQServer(String serverAddress) {
        super(serverAddress);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.printf("AvatarMQ Server Start Success![author tangjie]\n");
    }

    public void afterPropertiesSet() throws Exception {
        init();
        start();
    }
}
