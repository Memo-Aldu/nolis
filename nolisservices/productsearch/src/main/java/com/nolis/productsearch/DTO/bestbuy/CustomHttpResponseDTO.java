package com.nolis.productsearch.DTO.bestbuy;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
