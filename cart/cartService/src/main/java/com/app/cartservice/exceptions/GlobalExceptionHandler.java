package com.app.cartservice.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorResponse buildError(HttpStatus status, String message, HttpServletRequest request) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralError(Exception ex, HttpServletRequest request) {
        ApiErrorResponse error = buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
        ex.printStackTrace();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<?> handleEmptyCartException(Exception ex,HttpServletRequest request) {
        ApiErrorResponse error = buildError(HttpStatus.OK, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(UserNotLoggedInException.class)
    public ResponseEntity<?> handleUserNotLoggedInException(Exception ex,HttpServletRequest request) {
        ApiErrorResponse error = buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CartDoesNotExistsException.class)
    public ResponseEntity<?> handleCartDoesNotExistsException(Exception ex,HttpServletRequest request) {
        ApiErrorResponse error = buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<?> handleItemNotFoundException(Exception ex,HttpServletRequest request) {
        ApiErrorResponse error = buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


}




