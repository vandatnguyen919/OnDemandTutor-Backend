package com.mytutor.exceptions;

/**
 * @author vothimaihoa
 *
 */
public class SubjectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SubjectNotFoundException(String message) {
        super(message);
    }
}
