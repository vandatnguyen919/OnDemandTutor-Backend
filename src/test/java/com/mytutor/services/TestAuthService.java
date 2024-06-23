package com.mytutor.services;

import com.mytutor.dto.AuthenticationResponseDto;
import com.mytutor.dto.auth.LoginDto;
import com.mytutor.dto.auth.RegisterDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Otp;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.security.SecurityUtil;
import com.mytutor.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestAuthService {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private OtpService otpService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    public void Authentication_Login_VerifyLogin_LoginSuccess() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("Password123.");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(securityUtil.createToken(authentication)).thenReturn("dummy-jwt-token");

        // Act
        ResponseEntity<?> response = authService.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(AuthenticationResponseDto.class, response.getBody());
        AuthenticationResponseDto authResponse = (AuthenticationResponseDto) response.getBody();
        assertEquals("dummy-jwt-token", authResponse.getAccessToken());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/login_failure.csv", numLinesToSkip = 1)
    public void Authentication_Login_VerifyLogin_LoginFailure(String email, String password) {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.login(loginDto));

        // Assert
        assertEquals("Bad credentials", exception.getMessage());
    }

    @Test
    public void Authentication_Register_VerifyRegister_RegisterSuccess() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName("Test User");
        registerDto.setPhoneNumber("0912345678");
        registerDto.setPassword("Password123!");

        when(accountRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(accountRepository.existsByPhoneNumber(registerDto.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1); // Mock the account ID generation
            account.setCreatedAt(new Date());
            return account;
        });

        ResponseEntity<?> response = authService.register(registerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(AuthServiceImpl.AccountResponse.class, response.getBody());
        AuthServiceImpl.AccountResponse authResponse = (AuthServiceImpl.AccountResponse) response.getBody();
        assertEquals("test@example.com", authResponse.email());
        assertEquals("REGISTRATION", authResponse.status());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/register_failure.csv", numLinesToSkip = 1)
    public void Authentication_Register_VerifyRegister_RegisterFailure(
            String email,
            String phoneNumber
    ) {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setPhoneNumber(phoneNumber);

        if (phoneNumber == null)
            when(accountRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);
        else
            when(accountRepository.existsByPhoneNumber(registerDto.getPhoneNumber())).thenReturn(true);


        ResponseEntity<?> response = authService.register(registerDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(String.class, response.getBody());
        String message = (String) response.getBody();
        assertNotNull(message);
    }
}
