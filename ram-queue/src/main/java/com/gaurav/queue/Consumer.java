package com.gaurav.queue;

import com.gaurav.queue.model.Message;

public interface Consumer {
    void consumeAsync(String topic, String subscription, MessageListener messageListener);

    Message consume(String topic, String subscription);
}
