/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nguyen Van Dat
 */
public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";

    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomPassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length should be at least 4 characters");
        }

        StringBuilder password = new StringBuilder(length);
        List<Character> charCategories = new ArrayList<>();

        // Ensure the password contains at least one character from each category
        charCategories.add(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        charCategories.add(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        charCategories.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        charCategories.add(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the remaining characters
        for (int i = 4; i < length; i++) {
            charCategories.add(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle the characters to ensure randomness
        Collections.shuffle(charCategories);

        for (Character character : charCategories) {
            password.append(character);
        }

        return password.toString();
    }

    // Method to generate random OTP code
    public static String generateRandomOtpCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("OTP length should be greater than 0");
        }

        StringBuilder otp = new StringBuilder(length);
        // Generate the remaining digits
        for (int i = 0; i < length; i++) {
            otp.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }

        return otp.toString();
    }

    public static void main(String[] args) {
//        int passwordLength = 12; // Desired password length
//        String randomPassword = generateRandomPassword(passwordLength);
//        System.out.println("Generated Password: " + randomPassword);

        int otpLength = 6; // Desired OTP length
        String randomOtp = generateRandomOtpCode(otpLength);
        System.out.println("Generated OTP: " + randomOtp);
    }
}
