package com.example.paper.trading.exceptionHandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // Set the response Header
        response.setHeader("Error-Reason", "Authorization failed");
        // 403
        response.setStatus(HttpStatus.FORBIDDEN.value());
        // to transfer string format to json format of the response
        response.setContentType("application/json;charset=UTF-8");
        // construct the response
        String errorMessage = accessDeniedException.getMessage();
        String path = request.getRequestURI();
        String jsonResponse = String.format("{\"error\": \"%s\", \"path\": \"%s\"}", errorMessage, path);

        System.out.println(jsonResponse);
        response.getWriter().write(jsonResponse);
    }
}