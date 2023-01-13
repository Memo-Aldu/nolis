package com.nolis.commonconfig.security.service;

import com.nolis.commondata.dto.JWTAuthDTO;

public interface AuthService {
    boolean hasAuthority(String token, String authority);
    JWTAuthDTO decodeJWT(String token);
}
