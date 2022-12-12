package com.nolis.commonconfig.security.service;


import com.nolis.commondata.dto.http.CustomHttpResponseDTO;
import com.nolis.commondata.exception.HttpClientErrorException;
import com.nolis.commondata.exception.HttpExternalServerErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static com.nolis.commondata.constants.Auth.AUTH_SERVER_URL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
public class AuthServiceImp implements AuthService {

    private final RestTemplate restTemplate;


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
            return responseDTO != null && responseDTO.getData().get(authority) == Boolean.TRUE;
    }
}
