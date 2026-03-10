package com.borisedu.borisedu.exception.custom;

public class InvalidRequestInput extends RuntimeException {
    public InvalidRequestInput(String message) {
        super(message);
    }
}
