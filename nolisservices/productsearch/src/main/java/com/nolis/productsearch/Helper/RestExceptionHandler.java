package com.nolis.productsearch.Helper;

import com.nolis.productsearch.DTO.CustomHttpResponseDTO;
import com.nolis.productsearch.exception.HttpClientErrorException;
import com.nolis.productsearch.exception.HttpServerErrorException;
import lombok.AllArgsConstructor;
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
@ControllerAdvice @AllArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final ResponseHandler responseHandler;

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleEntityNotFound(
            HttpClientErrorException ex) throws IOException {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getHttpResponse().getStatusText()))
                        .timestamp(System.currentTimeMillis())
                        .status(ex.getHttpResponse().getStatusCode())
                        .success(false)
                        .message(ex.getLocalizedMessage())
                        .build(),
                ex.getHttpResponse().getHeaders());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    protected ResponseEntity<CustomHttpResponseDTO> handleEntityNotFound(
            HttpServerErrorException ex) throws IOException {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", ex.getHttpResponse().getBody().toString()))
                        .timestamp(System.currentTimeMillis())
                        .status(ex.getHttpResponse().getStatusCode())
                        .success(false)
                        .message(ex.getLocalizedMessage())
                        .build(),
                ex.getHttpResponse().getHeaders());
    }
}
