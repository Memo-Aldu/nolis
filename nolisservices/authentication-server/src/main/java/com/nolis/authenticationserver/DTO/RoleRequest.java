package com.nolis.authenticationserver.DTO;

public record RoleRequest(
    String authority,
    String id
) {
    public boolean isValid() {
        return authority != null || id != null;
    }
}
