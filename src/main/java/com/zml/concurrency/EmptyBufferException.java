package com.zml.concurrency;

public class EmptyBufferException extends RuntimeException{
    public EmptyBufferException(String message) {
        super(message);
    }
}
