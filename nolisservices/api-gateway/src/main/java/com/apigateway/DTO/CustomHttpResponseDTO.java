package com.apigateway.DTO;

import lombok.*;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Builder @Data
@AllArgsConstructor @NoArgsConstructor
public class CustomHttpResponseDTO {
    private boolean success;
    private long timestamp;
    private HttpStatus status;
    private Map<String, Object> data;
    private String message;

}

