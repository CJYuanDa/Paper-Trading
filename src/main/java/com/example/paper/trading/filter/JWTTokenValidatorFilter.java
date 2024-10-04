package com.example.paper.trading.filter;

import com.example.paper.trading.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Autowired
    public JWTTokenValidatorFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header  = jwtService.getJWTHeader();
        String jwt = request.getHeader(header);
        if (jwt != null) {
            try {
                Authentication authentication = jwtService.validate(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception exception) {
                throw new BadCredentialsException("Invalid token received");
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/header-login");
    }
}

// @Component
// public class backupJWTTokenValidatorFilter extends OncePerRequestFilter {
//
//     @Value("${jwt.key}")
//     private String secret;
//
//     @Value("${jwt.header}")
//     private String header;
//
//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//         String jwt = request.getHeader(header);
//         if (jwt != null) {
//             try {
//                 SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//                 Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();
//                 String username = (String) claims.get("username");
//                 String authorities = (String) claims.get("authorities");
//                 Authentication authentication = new UsernamePasswordAuthenticationToken(
//                         username, null, AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
//                 );
//                 SecurityContextHolder.getContext().setAuthentication(authentication);
//             } catch (Exception exception) {
//                 throw new BadCredentialsException("Invalid token received");
//             }
//         }
//
//         filterChain.doFilter(request, response);
//     }
//
//     @Override
//     protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//         return request.getServletPath().equals("/role-user");
//     }
// }
