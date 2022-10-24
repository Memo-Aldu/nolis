package com.nolis.authenticationserver.DTO;

public record AppUserRequest(
    String email,
    String id
) {
    public boolean isValid() {
        return email != null || id != null;
    }
}
