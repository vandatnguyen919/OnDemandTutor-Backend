package com.mytutor.config;

//import com.paypal.base.rest.APIContext;
//import com.paypal.core.PayPalEnvironment;
//import com.paypal.core.PayPalHttpClient;
import com.paypal.base.rest.APIContext;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {

//    @Value("${paypal.client-id}")
//    private String clientId;
//    @Value("${paypal.client-secret}")
//    private String clientSecret;
//    @Value("${paypal.mode}")
//    private String mode;
//
//    @Bean
//    public APIContext apiContext() {
//        return new APIContext(clientId, clientSecret, mode);
//    }
    @Bean
    public PayPalHttpClient getPaypalClient(
            @Value("${paypal.clientId}") String clientId,
            @Value("${paypal.clientSecret}") String clientSecret) {
        return new PayPalHttpClient(new PayPalEnvironment.Sandbox(clientId, clientSecret));
    }
}

