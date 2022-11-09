package com.nolis.productsearch.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nolis.productsearch.DTO.bestbuy.CustomHttpResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component @AllArgsConstructor
public class ResponseHandler {
    private final ObjectMapper objectMapper;
    public ResponseEntity<CustomHttpResponseDTO> httpResponse(
            CustomHttpResponseDTO customHttpResponseDTO , HttpHeaders headers) {
        if(headers == null) {
            headers = new HttpHeaders();
            headers.add("Content-Type", APPLICATION_JSON_VALUE);
        }
        try {
            return new ResponseEntity<>(customHttpResponseDTO, headers,
                    customHttpResponseDTO.getStatus());
        } catch (Exception e) {
            return InternalServerErrorResponse(e);
        }
    }

    public ResponseEntity<CustomHttpResponseDTO> InternalServerErrorResponse(Exception e) {
        return new ResponseEntity<>(internalServerErrorResponse(e), INTERNAL_SERVER_ERROR);
    }

    public HttpServletResponse jsonResponse (CustomHttpResponseDTO customHttpResponseDTO,
                                             HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON_VALUE);
        try {
            String json = objectMapper.writeValueAsString(
                    customHttpResponseDTO);
            response.getWriter().write(json);
            response.setStatus(customHttpResponseDTO.getStatus().value());
            response.getWriter().flush();
            return response;
        } catch (Exception e) {
            String errJson = null;
            try {
                errJson = objectMapper
                        .writeValueAsString(internalServerErrorResponse(e));
                response.getWriter().write(errJson);
                response.setStatus(INTERNAL_SERVER_ERROR.value());
                response.getWriter().flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return response;
        }
    }

    private CustomHttpResponseDTO internalServerErrorResponse(Exception e) {
        return CustomHttpResponseDTO.builder()
                .timestamp(System.currentTimeMillis())
                .status(INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .success(false)
                .data(new HashMap<>())
                .build();
    }
}


