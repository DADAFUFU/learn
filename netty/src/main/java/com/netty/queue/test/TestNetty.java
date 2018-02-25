package com.netty.queue.test;

import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sunfucheng on 2017/7/30.
 */
public class TestNetty {

    @Test
    public void test1() throws InterruptedException {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("123");
            }
        },1000,3000);

        Thread.sleep(100000);

        System.out.println("end");
    }
}
