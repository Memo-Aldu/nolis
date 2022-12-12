package com.nolis.commonconfig.security.service;

public interface AuthService {
    boolean hasAuthority(String token, String authority);
}
