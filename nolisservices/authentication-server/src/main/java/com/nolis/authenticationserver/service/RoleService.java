package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.RoleRequest;
import com.nolis.authenticationserver.modal.Role;

import java.util.List;

public interface RoleService {
    Role saveRole(Role role);
    List<Role> getRoles();

    Role getRoleByIdOrName(RoleRequest roleRequest);

    void deleteRoleByIdOrName(RoleRequest roleRequest);
}
