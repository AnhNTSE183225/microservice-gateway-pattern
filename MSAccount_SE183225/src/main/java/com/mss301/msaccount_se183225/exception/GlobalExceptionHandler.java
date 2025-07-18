package com.mss301.msaccount_se183225.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<?> buildResponse(HttpStatus status, Object error) {
        return ResponseEntity
                .status(status)
                .body(
                        Map.of(
                                "response", status.value(),
                                "error", error
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return buildResponse(BAD_REQUEST, errors);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class, UnauthorizedException.class})
    public ResponseEntity<?> handleUnauthorizedException(RuntimeException e) {
        return buildResponse(UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class, DisabledException.class})
    public ResponseEntity<?> handleForbiddenException(RuntimeException e) {
        return buildResponse(FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(BadRequestException e) {
        return buildResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeException(RuntimeException e) {
        return buildResponse(INTERNAL_SERVER_ERROR, e.getMessage());
    }
}