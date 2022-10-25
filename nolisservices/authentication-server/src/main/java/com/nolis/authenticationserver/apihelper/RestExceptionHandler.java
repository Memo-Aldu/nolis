package com.nolis.authenticationserver.apihelper;

import com.nolis.authenticationserver.DTO.CustomHttpResponseDTO;
import com.nolis.authenticationserver.exception.*;
import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice @AllArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final ResponseHandler responseHandler;

    @ExceptionHandler(AppEntityNotFoundException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleEntityNotFound(
            AppEntityNotFoundException ex) {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getMessage()))
                        .timestamp(System.currentTimeMillis())
                        .status(NOT_FOUND)
                        .success(false)
                        .message(ex.getLocalizedMessage())
                        .build(),
                null);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleBadRequest(
            BadRequestException ex) {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getMessage()))
                        .timestamp(System.currentTimeMillis())
                        .status(BAD_REQUEST)
                        .success(false)
                        .message(ex.getLocalizedMessage())
                        .build(),
                null);
    }

    @ExceptionHandler(AppEntityAlreadyExistException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleEntityAlreadyExist(
            AppEntityAlreadyExistException ex) {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getMessage()))
                        .timestamp(System.currentTimeMillis())
                        .status(CONFLICT)
                        .success(false)
                        .message(ex.getLocalizedMessage())
                        .build(),
                null);
    }

    @ExceptionHandler({TokenAuthenticationException.class,
    MissingAuthenticationException.class, AppAuthenticationException.class})
    protected ResponseEntity<CustomHttpResponseDTO> handleTokenAuthenticationException(
            RuntimeException ex) {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getMessage()))
                        .timestamp(System.currentTimeMillis())
                        .status(UNAUTHORIZED)
                        .success(false)
                        .message(ex.getLocalizedMessage())
                        .build(),
                null);
    }
}
