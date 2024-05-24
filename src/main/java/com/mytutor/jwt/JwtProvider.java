/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.jwt;

import com.mytutor.entities.Account;
import com.mytutor.repositories.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 *
 * @author Nguyen Van Dat
 */
@Component
public class JwtProvider {

    private static final String SECRET_KEY = "secretsecretsecretsecretsecretsecretsecretsecretsecretsecret";
    public static final long JWT_EXPIRATION = 2 * 7 * 24 * 60 * 60 * 1000L; // 2 weeks in milisecond
    public static final String TIME_ZONE = "Asia/Ho_Chi_Minh";
    
    @Autowired
    AccountRepository accountRepository;

    public String generateToken(UserDetails userDetails) {

        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + JWT_EXPIRATION);
        
        Account account = accountRepository.findByEmail(userDetails.getUsername()).get();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", account.getId());
        claims.put("email", account.getEmail());
        claims.put("fullName", account.getFullName());
        claims.put("authorities", userDetails.getAuthorities());

        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(claims)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
        return token;
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    public Date extractExpirationTime(String token) {
        return extractAllClaims(token).getExpiration();
    }
    
    public boolean isExpiredToken(String token) {
        return extractExpirationTime(token).before(new Date());
    }
    
    public boolean validateToken(String token) {
        return !isExpiredToken(token);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload(); 
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
