package com.nolis.authenticationserver.DTO;

public record RoleRequest(
    String name,
    String id
) {
    public boolean isValid() {
        return name == null && id == null;
    }
}
