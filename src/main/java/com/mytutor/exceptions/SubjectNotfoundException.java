package com.mytutor.exceptions;
/**
 *
 * @author vothimaihoa
 */
public class SubjectNotfoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public SubjectNotfoundException(String message) {
        super(message);
    }
}
