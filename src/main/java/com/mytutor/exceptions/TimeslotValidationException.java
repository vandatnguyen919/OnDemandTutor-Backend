package com.mytutor.exceptions;

/**
 * @author vothimaihoa
 *
 */
public class TimeslotValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public TimeslotValidationException(String message) {

        super(message);
    }
}
