package com.mytutor.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytutor.dto.AuthenticationResponseDto;
import com.mytutor.dto.auth.LoginDto;
import com.mytutor.dto.auth.RegisterDto;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestAuthController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountRepository accountRepository;

    @ParameterizedTest
    @CsvFileSource(resources = "/valid_emails.csv", numLinesToSkip = 1)
    public void Authentication_Login_NNS_46_VerifyEmail_CorrectEmailFormat(String email) throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword("Password123.");

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").doesNotExist());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_emails.csv", numLinesToSkip = 1)
    public void Authentication_Login_NNS_47_VerifyEmail_InvalidEmailFormat(String email, String description) throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        // Then
        response.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/valid_password.csv", numLinesToSkip = 1)
    public void Authentication_Login_NNS_48_VerifyPassword_ValidPasswordFormat(String password) throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword(password);

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").doesNotExist());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_password.csv", numLinesToSkip = 1)
    public void Authentication_Login_NNS_49_VerifyPassword_InvalidPasswordFormat(String password) throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword(password);

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        // Then
        response.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void Authentication_Login_NNS_50_VerifyLogin_LoginSuccess() throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("Password123.");

        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto("dummy-token");
        ResponseEntity<?> responseEntity = ResponseEntity.status(HttpStatus.OK).body(authenticationResponseDto);

        doReturn(responseEntity).when(authService).login(loginDto);

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists());
    }

    @Test
    public void Authentication_Login_NNS_51_VerifyLogin_LoginFailure() throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("nonexistent@example.com");
        loginDto.setPassword("FalsePa$$123");

        // Mock the response from authService.login() for non-existent account
        doThrow(new BadCredentialsException("Bad credentials")).when(authService).login(any(LoginDto.class));

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        // Then
        response.andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(401))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bad credentials"));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/valid_emails.csv", numLinesToSkip = 1)
    public void Authentication_Register_NNS_52_VerifyEmail_CorrectEmailFormat(String email) throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setFullName("Nguyen Van A");
        registerDto.setPhoneNumber("0368878548");
        registerDto.setPassword("Password123.");

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").doesNotExist());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_emails.csv", numLinesToSkip = 1)
    public void Authentication_Register_NSS_53_VerifyEmail_InvalidEmailFormat(String email, String description) throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setFullName("Nguyen Van A");
        registerDto.setPhoneNumber("0368878548");
        registerDto.setPassword("Password123.");

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        // Then
        response.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/valid_full_name_range.csv", numLinesToSkip = 1)
    public void Authentication_Register_NNS_54_VerifyFullName_1And1000CharacterFullName(String fullName) throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName(fullName);
        registerDto.setPhoneNumber("0368878548");
        registerDto.setPassword("Password123.");

        // When
        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").doesNotExist());
    }

//    @ParameterizedTest
//    @CsvFileSource(resources = "/invalid_full_name_range.csv", numLinesToSkip = 1)
//    public void Authentication_Register_NNS_55_VerifyFullName_0And1000CharacterFullName(String fullName) throws Exception {
//        // Given
//        RegisterDto registerDto = new RegisterDto();
//        registerDto.setEmail("test@example.com");
//        registerDto.setFullName(fullName);
//        registerDto.setPhoneNumber("0368878548");
//        registerDto.setPassword("Password123.");
//
//        // When
//        ResultActions response = mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerDto)));
//
//        // Then
//        response.andExpect(status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(400))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
//    }
//
//    @ParameterizedTest
//    @CsvFileSource(resources = "/valid_phone.csv", numLinesToSkip = 1)
//    public void Authentication_Register_NNS_56_VerifyPhone_ValidPhoneNumber(String phoneNumber) throws Exception {
//        // Given
//        RegisterDto registerDto = new RegisterDto();
//        registerDto.setEmail("test@example.com");
//        registerDto.setFullName("Nguyen Van A");
//        registerDto.setPhoneNumber(phoneNumber);
//        registerDto.setPassword("Password123.");
//
//        // When
//        ResultActions response = mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerDto)));
//
//        // Then
//        response.andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").doesNotExist());
//    }
//
//    @ParameterizedTest
//    @CsvFileSource(resources = "/invalid_phone.csv", numLinesToSkip = 1)
//    public void Authentication_Register_NNS_57_VerifyPhone_InvalidPhoneNumber(String phoneNumber) throws Exception {
//        // Given
//        RegisterDto registerDto = new RegisterDto();
//        registerDto.setEmail("test@example.com");
//        registerDto.setFullName("Nguyen Van A");
//        registerDto.setPhoneNumber(phoneNumber);
//        registerDto.setPassword("Password123.");
//
//        // When
//        ResultActions response = mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerDto)));
//
//        // Then
//        response.andExpect(status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(400))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
//    }
}