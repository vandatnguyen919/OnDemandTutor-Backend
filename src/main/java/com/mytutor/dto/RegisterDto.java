/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import lombok.Data;

/**
 *
 * @author Nguyen Van Dat
 */
@Data
public class RegisterDto {

    private String email;
    private String fullName;
    private String phoneNumber;
    private String password;
}
