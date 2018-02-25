#!/bin/bash

echo "mq-server start..."

JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home"

$JAVA_HOME/bin/java -cp ../learn-1.0-SNAPSHOT.jar com.netty.queue.spring.AvatarMQServerStartup