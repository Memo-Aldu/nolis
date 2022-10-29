package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.RoleRequest;
import com.nolis.authenticationserver.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    Role saveRole(Role role);
    List<Role> getRoles();

    Page<Role> getRoles(Pageable page);

    Role getRoleByIdOrName(RoleRequest roleRequest);

    void deleteRoleByIdOrName(RoleRequest roleRequest);
}
