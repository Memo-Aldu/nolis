package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.RoleRequest;
import com.nolis.authenticationserver.exception.AppEntityAlreadyExistException;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.repository.RoleRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service @Slf4j @AllArgsConstructor
public class RoleServiceImp implements RoleService {
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
    public Role getRoleByIdOrName(RoleRequest roleRequest) {
        log.info("Getting role {}", roleRequest);
        return roleRepo.findRoleByIdOrName(
                roleRequest.id(), roleRequest.name()).orElseThrow(
                () -> new IllegalStateException("Role not found")
        );
    }


    @Override
    public void deleteRoleByIdOrName(RoleRequest roleRequest) {
        log.info("Deleting role {}", roleRequest);
        roleRepo.deleteRoleByNameOrId(
                roleRequest.id(), roleRequest.name());
    }
}
