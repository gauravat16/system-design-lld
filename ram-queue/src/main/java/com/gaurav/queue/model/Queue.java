package com.gaurav.queue.model;

import java.util.Map;
import java.util.concurrent.locks.Lock;

public class Queue {
    public final Message[] messages;
    public final Lock lock;
    public int putPtr = 0;
    public int readPtr = -1;
    public int count = 0;
    public final Map<String, Subscription> subscriptions;

    public Queue(Message[] messages, Lock lock, Map<String, Subscription> subscriptions) {
        this.messages = messages;
        this.lock = lock;
        this.subscriptions = subscriptions;
    }

    public Message[] getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "Queue{" +
                ", putPtr=" + putPtr +
                ", readPtr=" + readPtr +
                ", count=" + count +
                ", subscriptions=" + subscriptions +
                '}';
    }
}
