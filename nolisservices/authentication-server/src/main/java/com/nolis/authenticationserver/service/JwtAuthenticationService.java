package com.nolis.authenticationserver.service;

import java.util.Map;

public interface JwtAuthenticationService {
    Map<String, Object> createTokenWitheRefreshToken(String refreshToken);
    Boolean authenticateToken(String token);

    Boolean isTokenExpired(String token);


}
