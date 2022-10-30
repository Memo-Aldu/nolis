package com.nolis.productsearch.service.consumer;

public interface AuthService {
    boolean hasAuthority(String token, String authority);
}
