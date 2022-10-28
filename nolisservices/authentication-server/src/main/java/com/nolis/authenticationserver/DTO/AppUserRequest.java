package com.nolis.authenticationserver.DTO;

public record AppUserRequest(
    String email,
    String password,
    String id
) {
    public boolean isValid() {
        return ((email != null || id != null) && password != null);
    }
}
