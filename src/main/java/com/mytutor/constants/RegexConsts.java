/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.constants;

import java.text.SimpleDateFormat;

/**
 *
 * @author vothimaihoa
 */
public interface RegexConsts {
    String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    String PHONE_NUMBER_REGEX = "^(0|\\+?84)(3|5|7|8|9)[0-9]{8}$";

    // Create a SimpleDateFormat object with the desired format
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
