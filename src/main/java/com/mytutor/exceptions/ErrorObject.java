/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.exceptions;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author HIEU
 */
@Data
public class ErrorObject {
    private int statusCode;
    private Object message;
    private Date timestamp;
}
