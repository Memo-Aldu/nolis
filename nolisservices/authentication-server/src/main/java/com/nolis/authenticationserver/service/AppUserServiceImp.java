package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.repository.AppUserRepo;
import com.nolis.authenticationserver.repository.RoleRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service @Slf4j
public record AppUserServiceImp(
        AppUserRepo appUserRepo,
        RoleRepo roleRepo) implements AppUserService {

    @Override
    public AppUser saveAppUser(AppUser appUser) {
        log.info("Saving new user {} to the database", appUser.toString());
        Optional<AppUser> OptionalAppUser = appUserRepo.findAppUserByEmail(
                appUser.getEmail()
        );
        if (OptionalAppUser.isPresent()) {
            log.info("User {} already exists", appUser.getEmail());
            throw new IllegalStateException("User already exists");
        }
        return appUserRepo.save(appUser);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.toString());
        Optional<Role> OptionalRole = roleRepo.findRoleByName(role.getName());
        if (OptionalRole.isPresent()) {
            log.info("Role {} already exists", role.getName());
            throw new IllegalStateException("Role already exists");
        }
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUserById(AddRoleRequest request) {
        log.info("Adding role {} to user {}", request.
                roleName(), request.userId());
        Optional<AppUser> OptionalAppUser = appUserRepo.
                findAppUserById(request.userId());
        Optional<Role> OptionalRole = roleRepo.
                findRoleByName(request.roleName());
        if (OptionalAppUser.isPresent() && OptionalRole.isPresent()) {
            AppUser appUser = OptionalAppUser.get();
            Role role = OptionalRole.get();
            appUser.getRoles().add(role);
        } else {
            log.info("User id {} or role {} does not exist"
                    , request.userId(), request.roleName());
            throw new IllegalStateException("User or role does not exist");
        }
    }

    @Override
    public AppUser getUserById(String id) {
        log.info("Getting user {}", id);
        Optional<AppUser> OptionalAppUser = appUserRepo.findAppUserById(id);
        if (OptionalAppUser.isPresent()) {
            return OptionalAppUser.get();
        } else {
            log.info("User {} does not exist", id);
            throw new IllegalStateException("User does not exist");
        }
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Getting all users");
        return appUserRepo.findAll();
    }

    @Override
    public List<Role> getRoles() {
        log.info("Getting all users");
        return roleRepo.findAll();
    }
}
