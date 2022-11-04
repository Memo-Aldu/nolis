package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.CustomHttpResponseDTO;
import com.nolis.productsearch.exception.TokenUnauthorizedToScopeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service @Slf4j
public record AuthServiceImp(
        @Qualifier("withEureka") RestTemplate restTemplate
) implements AuthService {

    private static final String AUTH_SERVER_URL = "http://authentication-server-service/api/v1/auth/has-authority";

    @Override
    public boolean hasAuthority(String token, String authority) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION,token);
        headers.set("scope", authority);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        CustomHttpResponseDTO responseDTO = restTemplate.exchange(
                        AUTH_SERVER_URL, HttpMethod.GET, request,
                CustomHttpResponseDTO.class).getBody();
        if( responseDTO != null && responseDTO.getData().get(authority) == Boolean.TRUE) {
            return true;
        }
        else {
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }
}
