package com.gaurav.queue.exception;

public class QException extends Exception{

    public QException(String message) {
        super(message);
    }

    public QException(String message, Throwable cause) {
        super(message, cause);
    }
}
