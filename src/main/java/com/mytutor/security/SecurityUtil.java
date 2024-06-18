package com.mytutor.security;

import com.mytutor.constants.RegexConsts;
import com.mytutor.entities.Account;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class SecurityUtil {

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${mytutor.jwt.base64-secret}")
    private String jwtKey;

    @Value("${mytutor.jwt.token-validity-in-second}")
    private long jwtExpiration;

    @Autowired
    AccountRepository accountRepository;

    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String createToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtExpiration, ChronoUnit.SECONDS);

        String email = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            email = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");
        } else {
            email = authentication.getName();
        }
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Consumer<Map<String, Object>> claimsConsumer = c -> {
            c.put("id", account.getId());
            c.put("email", account.getEmail());
            c.put("fullName", account.getFullName());
            c.put("avatarUrl", account.getAvatarUrl());
            c.put("createdAt", RegexConsts.sdf.format(account.getCreatedAt()));
            c.put("description", account.getDescription());
            c.put("status", account.getStatus());
            c.put("role", account.getRole());
        };

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claims(claimsConsumer)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
