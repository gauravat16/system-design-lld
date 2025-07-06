package com.gaurav;

import com.gaurav.queue.PubSub;
import com.gaurav.queue.exception.QException;
import com.gaurav.queue.impl.InMemoryPubSubImpl;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws QException {
        PubSub pubSub = new InMemoryPubSubImpl(10);
        pubSub.createTopic("t1");
        for (int i = 0; i < 10; i++) {
            pubSub.produce("t1", String.valueOf(i).getBytes(StandardCharsets.UTF_8));
        }
        pubSub.createSubscription("t1", "s1");
        pubSub.createSubscription("t1", "s2");

        for (int i = 0; i < 10; i++) {
            System.out.println("s1 -" + pubSub.consume("t1", "s1"));
        }
        for (int i = 0; i < 10; i++) {
            System.out.println("s2 -" + pubSub.consume("t1", "s2"));
        }

        for (int i = 10; i < 20; i++) {
            pubSub.produce("t1", String.valueOf(i).getBytes(StandardCharsets.UTF_8));
        }

        pubSub.createSubscription("t1", "s3");
        for (int i = 0; i < 10; i++) {
            System.out.println("s1 -" + pubSub.consume("t1", "s1"));
        }
        for (int i = 0; i < 10; i++) {
            System.out.println("s2 -" + pubSub.consume("t1", "s2"));
        }

        for (int i = 0; i < 10; i++) {
            System.out.println("s3 -" + pubSub.consume("t1", "s3"));
        }



    }
}