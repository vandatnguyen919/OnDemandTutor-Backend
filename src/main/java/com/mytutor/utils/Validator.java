package com.mytutor.utils;

import com.mytutor.constants.RegexConsts;
import com.mytutor.exceptions.InvalidInputException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.authentication.BadCredentialsException;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadCredentialsException("Email is required");
        }
        if (!isValidEmail(email)) {
            throw new BadCredentialsException("Invalid email format");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BadCredentialsException("Password is required");
        }
        if (!isValidPassword(password)) {
            throw new BadCredentialsException("Password must be between 8 and 16 characters, including at least 1 number, 1 uppercase character, 1 lowercase character, and 1 special character");
        }
    }

    public static void validateFullName(String fullName) {
        if (fullName == null) {
            throw new InvalidInputException("Full name is required");
        }
        if (fullName.isEmpty() || fullName.length() > 1000) {
            throw new InvalidInputException("Full name must be between 1 and 1000");
        }
        if (!isValidFullName(fullName)) {
            throw new InvalidInputException("Invalid full name");
        }
    }

    public static boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    public static boolean isValidPassword(String password) {
        // Compile the regex
        Pattern pattern = Pattern.compile(RegexConsts.PASSWORD_REGEX);
        // Create a matcher with given password
        Matcher matcher = pattern.matcher(password);
        // Return if the password matches the regex
        return matcher.matches();
    }

    public static boolean isValidFullName(String fullName) {
        Pattern pattern = Pattern.compile(RegexConsts.FULL_NAME_REGEX_UTF8);
        // Normalize the name to ensure consistent representation of accented characters
        String normalized = Normalizer.normalize(fullName, Normalizer.Form.NFC);
        // Match the normalized name against the pattern
        return pattern.matcher(normalized).matches();
    }
}
