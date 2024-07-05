package com.mytutor.exceptions;

/**
 *
 * @author vothimaihoa
 */
public class InvalidStatusException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InvalidStatusException(String message) {
        super(message);
    }
}
