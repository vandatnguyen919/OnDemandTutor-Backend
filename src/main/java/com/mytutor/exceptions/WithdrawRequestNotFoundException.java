package com.mytutor.exceptions;

public class WithdrawRequestNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WithdrawRequestNotFoundException(String message) {
        super(message);
    }
}
