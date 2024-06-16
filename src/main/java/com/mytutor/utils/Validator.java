package com.mytutor.utils;

import com.mytutor.constants.RegexConsts;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.exceptions.InvalidInputException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.authentication.BadCredentialsException;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s]+$");
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
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new InvalidInputException("Full name is required");
        }
        if (fullName.isEmpty() || fullName.length() > 1000) {
            throw new InvalidInputException("Full name must be between 1 and 1000");
        }
        if (!isValidFullName(fullName)) {
            throw new InvalidInputException("Invalid full name");
        }
    }

    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new InvalidInputException("Phone number is required");
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new InvalidInputException("Invalid phone number");
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

    public static boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile(RegexConsts.PHONE_NUMBER_REGEX);
        return pattern.matcher(phoneNumber).matches();
    }
    public static void validateEducation(EducationDto educationDto) throws Exception {
        if (educationDto.getUniversityName().trim().isEmpty()) {
            throw new Exception("University name cannot be blank");
        }
        if (!NAME_PATTERN.matcher(educationDto.getUniversityName()).matches()) {
            throw new Exception("University name cannot contain numbers or special characters");
        }
        if (educationDto.getUniversityName().charAt(0) == ' ') {
            throw new Exception("University name cannot start with a space");
        }

        if (educationDto.getDegreeType() == null || !isValidDegreeType(educationDto.getDegreeType())) {
            throw new Exception("Invalid degree type");
        }

        if (educationDto.getMajorName().trim().isEmpty()) {
            throw new Exception("Major name cannot be blank");
        }
        if (!NAME_PATTERN.matcher(educationDto.getMajorName()).matches()) {
            throw new Exception("Major name cannot contain numbers or special characters");
        }
        if (educationDto.getMajorName().charAt(0) == ' ') {
            throw new Exception("Major name cannot start with a space");
        }

        if (educationDto.getSpecialization().trim().isEmpty()) {
            throw new Exception("Specialization cannot be blank");
        }
        if (!NAME_PATTERN.matcher(educationDto.getSpecialization()).matches()) {
            throw new Exception("Specialization cannot contain numbers or special characters");
        }
        if (educationDto.getSpecialization().charAt(0) == ' ') {
            throw new Exception("Specialization cannot start with a space");
        }
        if (educationDto.getStartYear() == 0) {
            throw new Exception("Start Year must not be blank");
        }

        if (educationDto.getEndYear() == 0) {
            throw new Exception("End Year must not be blank");
        }
    }

    public static boolean isValidDegreeType(String degreeType) {
        return degreeType.equals("BACHELOR") || degreeType.equals("MASTER") || degreeType.equals("DOCTORATE");
    }
}
