package com.gaurav.queue.impl;

import com.gaurav.queue.PubSub;
import com.gaurav.queue.exception.QException;
import com.gaurav.queue.model.Message;
import com.gaurav.queue.model.Queue;
import com.gaurav.queue.model.Subscription;
import com.gaurav.queue.model.Topic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryPubSubImpl implements PubSub {

    private final int maxQueueLength;
    private final Map<String, Topic> topicQueueMapping = new ConcurrentHashMap<>();

    public InMemoryPubSubImpl(int maxQueueLength) {
        this.maxQueueLength = maxQueueLength;
    }

    @Override
    public void createTopic(String topic) throws QException {
        if (topicQueueMapping.containsKey(topic)) {
            throw new QException("Topic Already Created!");
        }
        topicQueueMapping.computeIfAbsent(topic, _ -> {
            Queue queue = new Queue(new Message[maxQueueLength], new ReentrantLock(), new HashMap<>());
            return new Topic(queue);
        });
    }

    @Override
    public void produce(final String topic, byte[] message) throws QException {
        if (!topicQueueMapping.containsKey(topic)) {
            throw new QException("Topic not Created!");
        }

        Queue queue = topicQueueMapping.get(topic).queue;
        Lock lock = queue.lock;
        try {
            lock.lock();

            if (queue.count == queue.messages.length) throw new QException("Queue is full!");
            queue.getMessages()[queue.putPtr] = new Message(message);
            queue.count++;
            if (queue.readPtr == -1 || queue.readPtr == queue.messages.length) {
                queue.readPtr = 0;
            }
            if (++queue.putPtr == queue.getMessages().length) {
                queue.putPtr = 0;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void createSubscription(String topic, String subscription) throws QException {
        if (!topicQueueMapping.containsKey(topic)) {
            throw new QException("Topic not Created!");
        }
        Queue queue = topicQueueMapping.get(topic).queue;
        Lock lock = queue.lock;
        try {
            lock.lock();
            if (queue.subscriptions.containsKey(subscription)) {
                throw new QException("Subscription already created");
            }
            queue.subscriptions.put(subscription, new Subscription(subscription, 0));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Message consume(String topic, String subscriptionName) throws QException {
        if (!topicQueueMapping.containsKey(topic)) {
            throw new QException("Topic not Created!");
        }
        Queue queue = topicQueueMapping.get(topic).queue;
        Lock lock = queue.lock;
        try {
            lock.lock();
            if (!queue.subscriptions.containsKey(subscriptionName)) {
                throw new QException("Subscription not created");
            }
            if (queue.count == 0) {
                throw new QException("Queue has no messages");
            }

            Subscription subscription = queue.subscriptions.get(subscriptionName);
            subscription.offset = subscription.offset == maxQueueLength ? queue.readPtr : subscription.offset;
            if (subscription.offset == maxQueueLength) {
                throw new QException("Queue has no new messages");
            }
            
            Message message = queue.messages[subscription.offset++];
            boolean allSubscriptionsHaveReadMessage = true;
            for (Subscription sub : queue.subscriptions.values()) {
                if (sub.offset <= queue.readPtr) {
                    allSubscriptionsHaveReadMessage = false;
                    break;
                }
            }

            if (allSubscriptionsHaveReadMessage) {
                queue.count--;
                queue.readPtr++;
            }
            return message;
        } finally {
            lock.unlock();
        }
    }
}
