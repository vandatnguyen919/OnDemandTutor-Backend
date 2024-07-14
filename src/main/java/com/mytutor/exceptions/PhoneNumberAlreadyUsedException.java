package com.mytutor.exceptions;

public class PhoneNumberAlreadyUsedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public PhoneNumberAlreadyUsedException(String message) {
        super(message);
    }
}
