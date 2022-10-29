package com.nolis.productsearch.Service.Consumer;

import com.nolis.productsearch.DTO.CustomHttpResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service @Slf4j
public record AuthServiceImp(
        RestTemplate restTemplate
) implements AuthService {

    private static final String AUTH_SERVER_URL = "http://authentication-server-service/api/v1/auth/has-authority";

    @Override
    public boolean hasAuthority(String token, String authority) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION,token);
        headers.set("scope", authority);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        CustomHttpResponseDTO responseDTO = restTemplate.exchange(
                        AUTH_SERVER_URL, HttpMethod.GET, request,
                CustomHttpResponseDTO.class).getBody();
        return responseDTO != null && responseDTO.isSuccess();
    }
}
