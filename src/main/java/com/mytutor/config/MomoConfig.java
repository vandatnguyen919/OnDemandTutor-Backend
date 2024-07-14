package com.mytutor.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MomoConfig {

    @Value("${mytutor.url.client}")
    private String clientUrl;

    @Value("${momo.partnerCode}")
    private String partnerCode;

    @Value("${momo.accessKey}")
    private String accessKey;

    @Value("${momo.secretKey}")
    private String momoSecretKey;

    @Value("${momo.returnUrl}")
    private String redirectUrl;

    @Value("${momo.ipnUrl}")
    private String ipnUrl;

    @Value("${momo.requestType}")
    private String requestType;

    @Value("${momo.apiUrl}")
    private String momoApiUrl;

    @Value("${momo.queryApiUrl}")
    private String momoQueryApiUrl;

    public static String momo_PartnerCode;
    public static String momo_AccessKey;
    public static String momo_SecretKey;
    public static String momo_RedirectUrl;
    public static String momo_IpnUrl;
    public static String momo_RequestType;
    public static String momo_ApiUrl;
    public static String momo_QueryApiUrl;

    @PostConstruct
    private void init() {
        MomoConfig.momo_PartnerCode = this.partnerCode;
        MomoConfig.momo_AccessKey = this.accessKey;
        MomoConfig.momo_SecretKey = this.momoSecretKey;
        MomoConfig.momo_RedirectUrl = clientUrl + this.redirectUrl;
        MomoConfig.momo_IpnUrl = clientUrl + this.ipnUrl;
        MomoConfig.momo_RequestType = this.requestType;
        MomoConfig.momo_ApiUrl = this.momoApiUrl;
        MomoConfig.momo_QueryApiUrl = this.momoQueryApiUrl;
    }
}
