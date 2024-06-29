/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

/**
 *
 * @author HIEU
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<ErrorObject> handleCredentialsException(Exception ex) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {
            AccountNotFoundException.class,
            EducationNotFoundException.class,
            CertificateNotFoundException.class,
            SubjectNotFoundException.class,
            QuestionNotFoundException.class,
            FeedbackNotFoundException.class,
            AppointmentNotFoundException.class
    })
    public ResponseEntity<ErrorObject> handleNotFoundException(Exception ex) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            TimeslotValidationException.class,
            PaymentFailedException.class,
            InvalidStatusException.class
    })
    public ResponseEntity<ErrorObject> handleBadRequestException(Exception ex) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            ConflictTimeslotException.class,
            PhoneNumberAlreadyUsedException.class
    })
    public ResponseEntity<ErrorObject> handleConflictException(Exception ex) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(HttpStatus.CONFLICT.value());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.CONFLICT);
    }
//
//    @ExceptionHandler(InvalidAppointmentStatusException.class)
//    public ResponseEntity<ErrorObject> handleInvalidAppointmentStatusException(InvalidAppointmentStatusException ex) {
//        ErrorObject errorObject = new ErrorObject();
//
//        errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        errorObject.setMessage(ex.getMessage());
//        errorObject.setTimestamp(new Date());
//
//        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(PaymentFailedException.class)
//    public ResponseEntity<ErrorObject> handlePaymentException(PaymentFailedException ex) {
//        ErrorObject errorObject = new ErrorObject();
//
//        errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        errorObject.setMessage(ex.getMessage());
//        errorObject.setTimestamp(new Date());
//
//        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject> handleBindException(MethodArgumentNotValidException ex) {
        ErrorObject errorObject = new ErrorObject();
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + ": " + errorMessage);
        });
        errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorObject.setMessage(errors);
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }




}
