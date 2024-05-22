/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */
@Data
@NoArgsConstructor
public class UpdateAccountDetailsDto {
    private Date dayOfBirth;
    private Boolean gender; // male: false, female: true
    private String address;
    private String avatarUrl;

}
