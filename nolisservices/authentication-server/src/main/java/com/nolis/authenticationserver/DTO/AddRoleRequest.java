package com.nolis.authenticationserver.DTO;

public record AddRoleRequest(
        String userId,
        String email,
        String authority
) {
    public boolean isValid() {
        return ((userId != null || email != null) && authority != null);
    }
}
