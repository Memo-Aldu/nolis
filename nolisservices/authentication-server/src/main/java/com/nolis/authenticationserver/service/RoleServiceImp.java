package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.RoleRequest;
import com.nolis.authenticationserver.exception.AppEntityAlreadyExistException;
import com.nolis.authenticationserver.exception.AppEntityNotFoundException;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.repository.RoleRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service @Slf4j @AllArgsConstructor
public class RoleServiceImp implements RoleService {

    private final String SUPER_ADMIN_ROLE_NAME = "ROLE_SUPER_ADMIN";
    private final String SUPER_ADMIN_ROLE_ID = "6350c6f5fad4304f62022d3f";
    private final RoleRepo roleRepo;
    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.toString());
        Optional<Role> OptionalRole = roleRepo.findRoleByName(role.getName());
        if (OptionalRole.isPresent()) {
            log.info("Role {} already exists", role.getName());
            throw new AppEntityAlreadyExistException("Role already exists");
        }
        return roleRepo.save(role);
    }

    @Override
    public List<Role> getRoles() {
        log.info("Getting all roles");
        return roleRepo.findAll();
    }

    @Override
    public Page<Role> getRoles(Pageable page) {
        log.info("Getting a page pf roles");
        return roleRepo.findAll(page);
    }

    @Override
    public Role getRoleByIdOrName(RoleRequest roleRequest) {
        log.info("Getting role {}", roleRequest);
        return roleRepo.findRoleByIdOrName(
                roleRequest.id(), roleRequest.name()).orElseThrow(
                () -> new IllegalStateException("Role not found")
        );
    }


    @Override
    public void deleteRoleByIdOrName(RoleRequest request) {
        //delete the user and check if the user exists
        log.info("Deleting role {}", request);
        if(!roleRepo.existsRoleByNameOrId(request.name(), request.id())) {
            log.warn("User {} does not exist", request);
            throw new AppEntityNotFoundException("Role does not exist, request: " + request);
        }
        if(Objects.equals(request.name(), SUPER_ADMIN_ROLE_NAME) || Objects.equals(request.id(), SUPER_ADMIN_ROLE_ID)) {
            log.warn("Cannot delete super admin role");
            throw new IllegalStateException("Cannot delete super admin role");
        }
        roleRepo.deleteRoleByNameOrId(request.name(), request.id());
    }
}
