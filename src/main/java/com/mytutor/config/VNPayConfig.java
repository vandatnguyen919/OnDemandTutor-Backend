package com.mytutor.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class VNPayConfig {

    @Value("${mytutor.url.client}")
    private String clientUrl;

    @Value("${vnp.payUrl}")
    private String vnpPayUrl;

    @Value("${vnp.returnUrl}")
    private String vnpReturnUrl;

    @Value("${vnp.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnp.secretKey}")
    private String secretKey2;

    @Value("${vnp.version}")
    private String vnpVersion;

    @Value("${vnp.apiUrl}")
    private String vnpApiUrl;

    public static String vnp_PayUrl;
    public static String vnp_ReturnUrl;
    public static String vnp_TmnCode;
    public static String secretKey;
    public static String vnp_Version;
    public static String vnp_ApiUrl;

    @PostConstruct
    private void init() {
        VNPayConfig.vnp_PayUrl = this.vnpPayUrl;
        VNPayConfig.vnp_ReturnUrl = clientUrl + this.vnpReturnUrl;
        VNPayConfig.vnp_TmnCode = this.vnpTmnCode;
        VNPayConfig.secretKey = this.secretKey2;
        VNPayConfig.vnp_Version = this.vnpVersion;
        VNPayConfig.vnp_ApiUrl = this.vnpApiUrl;
    }

    //Util for VNPAY
    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
