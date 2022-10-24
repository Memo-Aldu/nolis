package com.apigateway.DTO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public record ResponseHandler(
) {
    public static ResponseEntity<Object> httpResponse(String message, Object data,
                                                      HttpStatus status, Boolean success,
                                                      HttpHeaders headers) {
        try {
            Map<String, Object> response = Map.of(
                    "timestamp", System.currentTimeMillis(),
                    "message", message,
                    "data", data,
                    "status", status.value(),
                    "is_success", success
            );
            return new ResponseEntity<>(response, headers, status);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    internalServerErrorResponse(e), status);
        }

    }

    public static ResponseEntity<Object> completeHttpResponse(
            String message, Map<String, Object> response, HttpStatus status, Boolean success) {
        try {
            response.put("message", message);
            response.put("status", status.value());
            response.put("is_success", success);
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    internalServerErrorResponse(e), status);
        }

    }

    public static ResponseEntity<Object> httpBadResponse(
            String message, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
            Map<String, Object> response = Map.of(
                    "timestamp", System.currentTimeMillis(),
                    "message", message,
                    "status", status.value(),
                    "is_success", false,
                    "data", new Object());
            return new ResponseEntity<>(response, headers, status);
        } catch (Exception e) {

            return new ResponseEntity<>(
                    internalServerErrorResponse(e), status);
        }

    }

    private static Map<String, Object> internalServerErrorResponse(Exception e) {
        return Map.of(
                "timestamp", System.currentTimeMillis(),
                "httpStatus", INTERNAL_SERVER_ERROR,
                "error", e.getMessage(),
                "trace", e.getStackTrace(),
                "is_success", false
        );
    }
}


