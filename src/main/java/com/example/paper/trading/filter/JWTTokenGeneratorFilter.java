package com.example.paper.trading.filter;

import com.example.paper.trading.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Autowired
    public JWTTokenGeneratorFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String header = jwtService.getJWTHeader();
            String jwt = jwtService.generate(authentication);
            response.setHeader(header, jwt);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean isLogin = request.getServletPath().equals("/header-login");
        return !isLogin;
    }
}

// @Component
// public class backupJWTTokenGeneratorFilter extends OncePerRequestFilter {
//
//     @Value("${jwt.key}")
//     private String secret;
//     @Value("${jwt.header}")
//     private String header;
//
//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         if (authentication != null) {
//             SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//             String jwt = Jwts
//                     .builder()
//                     .issuer("Paper-Trading")
//                     .subject("JWT Token")
//                     .claim("username", authentication.getName())
//                     .claim("authorities", authentication.getAuthorities()
//                             .stream()
//                             .map(authority -> authority.getAuthority())
//                             .collect(Collectors.joining(",")))
//                     .issuedAt(new Date())
//                     .expiration(new Date(System.currentTimeMillis() + 30000000)) // milliseconds
//                     .signWith(secretKey)
//                     .compact();
//             response.setHeader(header, jwt);
//         }
//         filterChain.doFilter(request, response);
//     }
//
//     @Override
//     protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//         // String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//         boolean isLogin = request.getServletPath().equals("/role-user");
//
//         return !isLogin;
//
//         // else if (authorizationHeader != null && authorizationHeader.startsWith("Basic")) {
//         //     return false;
//         // } else if (SecurityContextHolder.getContext().getAuthentication() == null) {
//         //     return false;
//         // }
//     }
// }