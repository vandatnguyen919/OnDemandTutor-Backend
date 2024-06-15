package com.mytutor.exceptions;

/**
 *
 * @author vothimaihoa
 */
public class AppointmentNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
