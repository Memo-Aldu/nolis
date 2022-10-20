package com.nolis.authenticationserver.DTO;

public record AddRoleRequest(
        String userId,
        String roleName
) {}
