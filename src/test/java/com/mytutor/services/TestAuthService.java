package com.mytutor.services;

import com.mytutor.dto.AuthenticationResponseDto;
import com.mytutor.dto.ForgotPasswordDto;
import com.mytutor.dto.LoginDto;
import com.mytutor.dto.RegisterDto;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.security.SecurityUtil;
import com.mytutor.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestAuthService {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/nns15.csv", numLinesToSkip = 1)
    public void Login_NNS_15_VerifyEmail_EmailIsRequired(String email) {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
//        loginDto.setPassword(password);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.login(loginDto));

        // Assert the exception message
        assertEquals("Email is required", exception.getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_email_format.csv", numLinesToSkip = 1)
    public void Login_NNS_16_VerifyEmail_CorrectEmailFormat(String email, String password) {

        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.login(loginDto));

        // Assert the exception message
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void NNS_17_VerifyEmail_FirstCharacterCannotHaveSpace() {

        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(" test@example.com");
        loginDto.setPassword("Password123.");

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.login(loginDto));

        // Assert the exception message
        assertEquals("First character can not have space", exception.getMessage());
    }

    @Test
    public void Login_NNS_18_VerifyPassword_PasswordIsRequired() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("");

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.login(loginDto));

        // Assert the exception message
        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    public void Login_NNS_19_VerifyPassword_FirstCharacterCannotHaveSpace() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword(" Password123.");

        // Act and Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.login(loginDto));

        // Assert the exception message
        assertEquals("First character can not have space", exception.getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_password.csv", numLinesToSkip = 1)
    public void Login_NNS_20_VerifyPassword_Must_Be_Between_8_And_16_Characters_Including_At_Least_1_Number_1_UpperCase_Character_1_LowerCase_Character_1_Special_Character(
            String email,
            String password
    ) {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword(password);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.login(loginDto));

        // Assert the exception message
        assertEquals("Password must be between 8 and 16 characters, including at least 1 number, 1 uppercase character, 1 lowercase character, and 1 special character", exception.getMessage());
    }

    @Test
    public void Login_NNS_159_LoginSuccess() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("Password123@");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(securityUtil.createToken(authentication)).thenReturn("fake-jwt-token");

        // Act
        ResponseEntity<?> response = authService.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(AuthenticationResponseDto.class, response.getBody());
        AuthenticationResponseDto authResponse = (AuthenticationResponseDto) response.getBody();
        assertEquals("fake-jwt-token", authResponse.getAccessToken());
    }

    @Test
    public void Login_NNS_160_Login_Failure() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("WrongPass123@");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.login(loginDto));

        // Assert
        assertEquals("Bad credentials", exception.getMessage());
    }

    @Test
    public void Register_NNS_22_VerifyEmail_EmailIsRequired() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
//        registerDto.setEmail("");
//        registerDto.setFullName("Nguyen Van A");
//        registerDto.setPhoneNumber("0123456789");
//        registerDto.setPassword("Password123@");

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Email is required", exception.getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_email_format.csv", numLinesToSkip = 1)
    public void Register_NNS_23_VerifyEmail_CorrectEmailFormat(String email) {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
//        registerDto.setFullName("Nguyen Van A");
//        registerDto.setPhoneNumber("0123456789");
//        registerDto.setPassword("Password123@");

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Invalid email format", exception.getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/full_name_with_numbers.csv", numLinesToSkip = 1)
    public void Register_NNS_25_VerifyFullName_NumbersAreNotAllowed(String fullName) {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName(fullName);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Invalid full name", exception.getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/full_name_with_special_characters.csv", numLinesToSkip = 1)
    public void Register_NNS_26_VerifyFullName_SpecialCharactersAreNotAllowed(String fullName) {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName(fullName);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Invalid full name", exception.getMessage());
    }

    @Test
    public void Register_NNS_27_VerifyFullName_FullNameMustNotBeBlank() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName("");

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Full name is required", exception.getMessage());
    }

//    @Test
//    public void NNS_28_VerifyFullName_FirstCharacterCannotHaveSpace(String fullName) {
//
//    }

    @ParameterizedTest
    @CsvFileSource(resources = "/full_name_length_0_or_1001.csv", numLinesToSkip = 1)
    public void Register_NNS_29_VerifyFullName_LengthMustBeBetween1To1000Characters(String fullName) {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName(fullName);

        System.out.println(fullName.length());

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Full name must be between 1 and 1000", exception.getMessage());
    }

    @Test
    public void Register_NNS_30_VerifyPhone_PhoneIsRequired() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName("Nguyen Van A");
//        registerDto.setPhoneNumber(" ");

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Phone number is required", exception.getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_phone_number_format.csv", numLinesToSkip = 1)
    public void Register_NNS_31_VerifyPhone_CorrectPhoneFormat(String phoneNumber) {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName("Nguyen Van A");
        registerDto.setPhoneNumber(phoneNumber);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Invalid phone number", exception.getMessage());
    }

//    public void NNS_33_VerifyPhone_PhoneRequires10Numbers() {
//
//    }

    @Test
    public void Register_NNS_33_VerifyPassword_PasswordIsRequired() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName("Nguyen Van A");
        registerDto.setPhoneNumber("0987654321");
//        registerDto.setPassword(" ");

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Password is required", exception.getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_password.csv", numLinesToSkip = 1)
    public void Register_NNS_35_VerifyPassword_Must_Be_Between_8_And_16_Characters_Including_At_Least_1_Number_1_UpperCase_Character_1_LowerCase_Character_1_Special_Character(
            String email,
            String password
    ) {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFullName("Nguyen Van A");
        registerDto.setPhoneNumber("0987654321");
        registerDto.setPassword(password);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.register(registerDto));

        // Assert the exception message
        assertEquals("Password must be between 8 and 16 characters, including at least 1 number, 1 uppercase character, 1 lowercase character, and 1 special character", exception.getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_email_format.csv", numLinesToSkip = 1)
    public void ForgetPassword_NNS_37_VerifyEmail_CorrectEmailFormat(
            String email,
            String password
        ) {
        // Arrange
        ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto();
        forgotPasswordDto.setEmail(email);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.forgotPassword(forgotPasswordDto));

        // Assert (Expected value)
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void ForgotPassword_NNS_38_VerifyEmail_EmailMustNotBeBlank() {
        // Arrange
        ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto();
        forgotPasswordDto.setEmail("");

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> authService.forgotPassword(forgotPasswordDto));

        // Assert (Expected value)
        assertEquals("Email is required", exception.getMessage());
    }
}
