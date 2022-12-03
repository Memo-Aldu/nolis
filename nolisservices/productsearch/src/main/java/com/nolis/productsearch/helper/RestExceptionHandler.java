package com.nolis.productsearch.helper;

import com.nolis.commondata.dto.http.CustomHttpResponseDTO;
import com.nolis.commondata.exception.BadRequestException;
import com.nolis.commondata.exception.ServerErrorException;
import com.nolis.productsearch.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice @AllArgsConstructor @Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final ResponseHandler responseHandler;

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleHttpClientError(
            HttpClientErrorException ex) throws IOException {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getHttpResponse().getStatusText()))
                        .timestamp(System.currentTimeMillis())
                        .status(ex.getHttpResponse().getStatusCode())
                        .success(false)
                        .message(ex.getLocalizedMessage())
                        .build(),
                headers(ex.getMessage()));
    }

    @ExceptionHandler(HttpExternalServerErrorException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleHttpServerError(
            HttpExternalServerErrorException ex) throws IOException {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getHttpResponse().getBody().toString()))
                        .timestamp(System.currentTimeMillis())
                        .status(ex.getHttpResponse().getStatusCode())
                        .success(false)
                        .message(ex.getLocalizedMessage())
                        .build(),
                headers(ex.getMessage()));
    }

    @ExceptionHandler(TokenUnauthorizedToScopeException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleUnauthorizedExceptions(
            TokenUnauthorizedToScopeException ex) {
        log.warn("Sending an unauthorized response: {}", ex.getMessage());
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

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleBadRequest(
            BadRequestException ex) {
        log.warn("Sending a bad request response: {}", ex.getMessage());
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

    @ExceptionHandler(ServerErrorException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleServerError(ServerErrorException ex) {
        log.warn("Sending a server error response: {}", ex.getMessage());
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getMessage()))
                        .timestamp(System.currentTimeMillis())
                        .status(INTERNAL_SERVER_ERROR)
                        .success(false)
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
