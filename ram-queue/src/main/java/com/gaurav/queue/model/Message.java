package com.gaurav.queue.model;

import java.util.Arrays;

public class Message {
    private byte[] message;

    public Message(byte[] message) {
        this.message = message;
    }

    public byte[] getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message=" + new String(message) +
                '}';
    }
}
