package com.nolis.authenticationserver.apihelper;

import com.nolis.authenticationserver.exception.*;
import com.nolis.commondata.dto.CustomHttpResponseDTO;
import com.nolis.commondata.exception.AppEntityAlreadyExistException;
import com.nolis.commondata.exception.AppEntityNotFoundException;
import com.nolis.commondata.exception.BadRequestException;
import com.nolis.commondata.exception.UnauthorizedTokenException;
import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
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
                headers(ex.getMessage()));
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
                headers(ex.getMessage()));
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
                headers(ex.getMessage()));
    }

    @ExceptionHandler({TokenAuthenticationException.class,
            InvalidTokenException.class, MissingAuthenticationException.class})
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
                headers(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedTokenException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleUnauthorizedException(
            UnauthorizedTokenException ex) {
        if(ex.getScope() == null) {
            return responseHandler.httpResponse(
                    CustomHttpResponseDTO.builder()
                            .data(Map.of("error", ex.getMessage()))
                            .timestamp(System.currentTimeMillis())
                            .status(OK)
                            .success(false)
                            .message(ex.getLocalizedMessage())
                            .build(),
                    headers(ex.getMessage()));
        }
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getMessage()))
                        .timestamp(System.currentTimeMillis())
                        .status(OK)
                        .success(true)
                        .data(Map.of(ex.getScope(), false))
                        .message(ex.getLocalizedMessage())
                        .build(),
                headers(ex.getMessage()));
    }

    private HttpHeaders headers(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        String ERROR_TYPE_HEADER = "error_type";
        headers.add(ERROR_TYPE_HEADER, message);
        return headers;
    }
}
