package com.example.paper.trading.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JWTService {

    @Value("${jwt.key}")
    private String secret;
    @Value("${jwt.header}")
    private String header;
    @Value("${jwt.expiration}")
    private long expiration;

    public String generate(Authentication authentication) {

        String username = authentication.getName();
        String authorities = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts
                .builder()
                .issuer("Paper-Trading")
                .subject("JWT Token")
                .claim("username", username)
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    public Authentication validate(String jwt) {
        Claims claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(jwt).getPayload();
        String username = (String) claims.get("username");
        String authorities = (String) claims.get("authorities");
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
        );
    }

    public String getJWTHeader() {
        return header;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
