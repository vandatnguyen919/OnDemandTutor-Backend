/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.exceptions;

/**
 *
 * @author Nguyen Van Dat
 */
public class AppointmentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
