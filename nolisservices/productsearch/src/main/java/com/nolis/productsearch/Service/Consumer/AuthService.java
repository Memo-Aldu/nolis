package com.nolis.productsearch.Service.Consumer;

public interface AuthService {
    boolean hasAuthority(String token, String authority);
}
