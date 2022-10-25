package com.nolis.authenticationserver.DTO;

public record EmailPasswordAuthenticationRequest(
        String email,
        String password
) {
    public boolean isValid() {
        return email != null && password != null;
    }
}
