package com.gaurav.queue;

import com.gaurav.queue.exception.QException;
import com.gaurav.queue.model.Message;

/**
 * 1. Allow consumers on a topic
 * 2. Allow publishers on a topic
 * 3. Each consumer should have its own cursor in the queue.
 * 4. Each Message will have TTL (only if all consumers have the read the messaged i.e.
 * it has been acked by all consumers
 */
public interface PubSub {

    void createTopic(String topic) throws QException;

    void produce(String topic, byte[] message) throws QException;

    void createSubscription(String topic, String subscription) throws QException;

    Message consume(String topic, String subscription) throws QException;
}
