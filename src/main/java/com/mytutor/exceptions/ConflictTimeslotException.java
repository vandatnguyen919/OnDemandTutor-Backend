package com.mytutor.exceptions;

/**
 *
 * @author vothimaihoa
 */
public class ConflictTimeslotException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public ConflictTimeslotException(String message) {
        super(message);
    }
}
