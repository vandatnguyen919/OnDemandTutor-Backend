package com.mytutor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytutor.dto.AuthenticationResponseDto;
import com.mytutor.dto.LoginDto;
import com.mytutor.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestAuthController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_emails.csv", numLinesToSkip = 1)
    public void Login_InvalidEmailFormat(String email, String description) throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword("Password123.");

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

//    @Test
//    public void Login_ValidEmailFormat() throws Exception {
//        // Given
//        LoginDto loginDto = new LoginDto();
//        loginDto.setEmail("test@example.com");
//        loginDto.setPassword("Password123.");
//
//        ResponseEntity<?> res = authService.login(loginDto);
//
//        when(authService.login(loginDto)).thenReturn(res);
//
//        // When
//        ResultActions response = mockMvc.perform(post("/api/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(loginDto)));
//
//        // Then
//        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(400))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
//    }
}