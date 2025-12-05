package com.app.cartservice.exceptions;

import feign.FeignException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(Exception ex,HttpServletRequest request) {
        ApiErrorResponse error = buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiErrorResponse> handleFeignNotFound(FeignException.NotFound ex, HttpServletRequest request) {

        ApiErrorResponse error = buildError(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND_IN_REMOTE_SERVICE",
                request
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RemoteServiceException.class)
    public ResponseEntity<?> handleRemoteServiceException(RemoteServiceException ex, HttpServletRequest request) {

        ApiErrorResponse error = buildError(
                HttpStatus.BAD_GATEWAY,
                ex.getMessage(),
                request
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }


    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiErrorResponse> handleFeignGeneral(FeignException ex, HttpServletRequest request) {

        // If Feign returned specific HTTP status
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiErrorResponse error = buildError(
                status,
                "REMOTE_SERVICE_ERROR: " + ex.getMessage(),
                request
        );

        return new ResponseEntity<>(error, status);
    }







}




