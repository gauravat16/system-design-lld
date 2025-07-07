package com.gaurav.consistent_hashing;

public class ConsistentHashingException extends Exception{

    public ConsistentHashingException(String message) {
        super(message);
    }

    public ConsistentHashingException(String message, Throwable cause) {
        super(message, cause);
    }
}
