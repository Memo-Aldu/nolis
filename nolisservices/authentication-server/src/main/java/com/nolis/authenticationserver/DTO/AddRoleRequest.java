package com.nolis.authenticationserver.DTO;

import javax.validation.constraints.NotNull;

public record AddRoleRequest(
        String userId,
        String email,
        @NotNull
        String roleName
) {
    public boolean isValid() {
        return userId != null || email != null && roleName != null;
    }
}
