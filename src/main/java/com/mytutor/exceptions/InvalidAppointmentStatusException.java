package com.mytutor.exceptions;

/**
 *
 * @author vothimaihoa
 */
public class InvalidAppointmentStatusException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InvalidAppointmentStatusException(String message) {
        super(message);
    }
}
