package com.example.library.common;

public class BorrowNotAllowedException extends RuntimeException {
    public BorrowNotAllowedException(String message) {
        super(message);
    }
}
