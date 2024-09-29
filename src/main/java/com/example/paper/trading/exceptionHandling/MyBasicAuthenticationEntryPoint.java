package com.example.paper.trading.exceptionHandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class MyBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Set the response header
        response.setHeader("Error-Reason", "Authentication failed");
        // 401
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // to transfer string format to json format of the response
        response.setContentType("application/json;charset=UTF-8");
        // construct the response
        String path = request.getRequestURI();
        String errorMessage = authException.getMessage();
        String jsonResponse = String.format("{\"error\": \"%s\", \"path\": \"%s\"}", errorMessage, path);

        System.out.println(jsonResponse);
        response.getWriter().write(jsonResponse);
    }
}
