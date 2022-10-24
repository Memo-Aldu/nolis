package com.nolis.authenticationserver.apihelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nolis.authenticationserver.DTO.CustomHttpResponseDTO;
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

    private CustomHttpResponseDTO internalServerErrorResponse(Exception e) {
        return CustomHttpResponseDTO.builder()
                .timestamp(System.currentTimeMillis())
                .status(INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .success(false)
                .data(new HashMap<>())
                .build();
    }

    public HttpServletResponse jsonResponse (CustomHttpResponseDTO customHttpResponseDTO,
                                             HttpServletResponse response)
            throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        try {
            String json = objectMapper.writeValueAsString(
                    customHttpResponseDTO);
            response.getWriter().write(json);
            response.setStatus(customHttpResponseDTO.getStatus().value());
            response.getWriter().flush();
            return response;
        } catch (Exception e) {
            String errJson =  objectMapper
                    .writeValueAsString(internalServerErrorResponse(e));
            response.getWriter().write(errJson);
            response.setStatus(INTERNAL_SERVER_ERROR.value());
            response.getWriter().flush();
            return response;
        }
    }
}


