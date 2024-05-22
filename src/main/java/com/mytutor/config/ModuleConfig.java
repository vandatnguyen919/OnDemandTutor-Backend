/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Principal;

/**
 *
 * @author vothimaihoa
 */
@Configuration
public class ModuleConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
