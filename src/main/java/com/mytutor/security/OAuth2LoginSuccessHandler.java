package com.mytutor.security;

import com.mytutor.dto.auth.AuthenticationResponseDto;
import com.mytutor.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${mytutor.url.client}")
    private String clientUrl;

    public OAuth2LoginSuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        ResponseEntity<?> res = authService.loginOAuthGoogle((OAuth2AuthenticationToken) authentication);
        String url;
        if (res.getBody() instanceof AuthenticationResponseDto) {
            // remove JSESSIONID
            Cookie cookie = new Cookie("JSESSIONID", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            String token = ((AuthenticationResponseDto) res.getBody()).getAccessToken();
            url = clientUrl + "/" + "?success=true&accessToken=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        } else {
            String message = "You are banned";
            url = clientUrl + "/" + "?success=false&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        }
        response.sendRedirect(url);
    }

}
