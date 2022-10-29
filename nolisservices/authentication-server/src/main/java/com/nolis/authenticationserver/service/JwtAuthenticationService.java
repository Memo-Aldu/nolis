package com.nolis.authenticationserver.service;

import java.util.Map;

public interface JwtAuthenticationService {
    Map<String, Object> createTokenWitheRefreshToken(String refreshToken);
    boolean authenticateToken(String token);
    boolean isAuthorized(String token, String scope);
}
