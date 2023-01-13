package com.nolis.commonconfig.security.service;


import com.nolis.commondata.dto.CustomHttpResponseDTO;
import com.nolis.commondata.dto.JWTAuthDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

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
                    AUTH_SERVER_URL + "/has-authority", HttpMethod.GET, request,
                    CustomHttpResponseDTO.class).getBody();
            return responseDTO != null && responseDTO.getData().get(authority) == Boolean.TRUE;
    }

    @Override
    public JWTAuthDTO decodeJWT(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION,token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        CustomHttpResponseDTO responseDTO = restTemplate.exchange(
                AUTH_SERVER_URL+ "/decode", HttpMethod.POST, request,
                CustomHttpResponseDTO.class).getBody();
        if(responseDTO != null && responseDTO.getData() != null) {
            return new JWTAuthDTO(
                    responseDTO.getData().get("access_token").toString(),
                    (ArrayList<String>) responseDTO.getData().get("authorities"),
                    responseDTO.getData().get("subject").toString()
            );
        }
        return null;
    }
}
