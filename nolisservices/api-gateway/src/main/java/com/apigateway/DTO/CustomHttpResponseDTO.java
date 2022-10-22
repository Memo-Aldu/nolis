package com.apigateway.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@ToString
public class CustomHttpResponseDTO {
    private long timestamp;
    private int status;
    private Object data;
    private String message;
    private boolean is_success;

    public CustomHttpResponseDTO(long timestamp, int status, Object data, String message, boolean is_success) {
        this.timestamp = timestamp;
        this.status = status;
        this.data = data;
        this.message = message;
        this.is_success = is_success;
    }
}
