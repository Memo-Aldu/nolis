package com.nolis.authenticationserver.DTO;

public record AddRoleRequest(
        String userId,
        String email,
        String roleName
) {
    public boolean isValid() {
        return ((userId != null || email != null) && roleName != null);
    }
}
