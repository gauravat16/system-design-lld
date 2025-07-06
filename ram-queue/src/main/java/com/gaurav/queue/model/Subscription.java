package com.gaurav.queue.model;

public class Subscription {
    public String subscriptionName;
    public int offset;

    public Subscription(String subscription, int offset) {
        this.subscriptionName = subscription;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "subscriptionName='" + subscriptionName + '\'' +
                ", offset=" + offset +
                '}';
    }
}
