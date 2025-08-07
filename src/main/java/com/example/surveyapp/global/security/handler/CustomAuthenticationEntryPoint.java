
package com.example.surveyapp.global.security.handler;

import com.example.surveyapp.global.response.ApiResponse;
import com.example.surveyapp.global.response.exception.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        writeErrorResponse(response, errorResponse);
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorResponseDto error) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(error.getMessage(), error));
    }
}

