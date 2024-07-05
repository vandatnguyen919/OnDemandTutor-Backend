package com.mytutor.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytutor.exceptions.ErrorObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final AccessDeniedHandler delegate = new BearerTokenAccessDeniedHandler();

    private final ObjectMapper mapper;

    public CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.delegate.handle(request, response, accessDeniedException);
        response.setContentType("application/json;charset=UTF-8"); // Support Vietnamese language

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorObject.setMessage("You have no permission to access this resource");
        errorObject.setTimestamp(new Date());

        mapper.writeValue(response.getWriter(), errorObject);
    }
}
