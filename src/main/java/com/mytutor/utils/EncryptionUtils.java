package com.mytutor.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class EncryptionUtils {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_SHA512 = "HmacSHA512";

    public static String hmacSHA(String key, String data, String algorithm) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac mac = Mac.getInstance(algorithm);
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, algorithm);
            mac.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = mac.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (IllegalStateException | NullPointerException | InvalidKeyException | NoSuchAlgorithmException ex) {
            return "";
        }
    }

    public static String hmacSHA256(String secretKey, String data) {
        return hmacSHA(secretKey, data, HMAC_SHA256);
    }

    public static String hmacSHA512(String secretKey, String data) {
        return hmacSHA(secretKey, data, HMAC_SHA512);
    }
}
