package com.revshop_backend.exception;

public class NoOrdersFoundException extends RuntimeException {

    public NoOrdersFoundException(String message) {
        super(message);
    }
}