package com.ywcheong.short4.exception;

public class MongoFailureException extends RuntimeException {
    public MongoFailureException(String message) {
        super(message);
    }
}
