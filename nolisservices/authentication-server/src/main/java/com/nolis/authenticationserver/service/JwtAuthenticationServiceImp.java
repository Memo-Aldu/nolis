package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.configuration.JwtConfig;
import com.nolis.authenticationserver.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service @Slf4j
public record JwtAuthenticationServiceImp(
        JwtUtils jwtUtils,
        JwtConfig jwtConfig
) implements JwtAuthenticationService {

    @Override
    public Map<String, Object> createTokenWitheRefreshToken(String refreshToken) {
        String newToken = jwtUtils.createTokenWithRefreshToken(refreshToken);
        Map<String, Object> responseBody = new HashMap<>();
        log.info("New token is : {}", newToken);
        responseBody.put(jwtConfig.accessHeader(), newToken);
        responseBody.put(jwtConfig.refreshHeader(), refreshToken);
        return responseBody;
    }

    @Override
    public boolean authenticateToken(String authorizationHeader) {
        Authentication authentication = jwtUtils.authenticateToken(authorizationHeader);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User {} is authenticated : {} ",
                authentication.getName(), authentication.isAuthenticated());
        return authentication.isAuthenticated();
    }

    @Override
    public boolean isAuthorized(String token, String scope) {
        String[] authorities = jwtUtils.getAuthoritiesFromToken(token);
        if(authorities != null) {
            for (String authority : authorities) {
                log.info("Authority : {}", authority);
                if (authority.equals(scope)) {
                    return true;
                }
            }
        }
        return false;
    }


}
