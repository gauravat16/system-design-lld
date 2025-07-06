package com.gaurav.queue;

public interface Producer {
    int produce(String topic, byte[] message);
}
