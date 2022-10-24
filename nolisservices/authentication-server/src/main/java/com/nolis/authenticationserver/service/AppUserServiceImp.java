package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.repository.AppUserRepo;
import com.nolis.authenticationserver.repository.RoleRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service @Slf4j
public record AppUserServiceImp(
        AppUserRepo appUserRepo,
        RoleRepo roleRepo,
        BCryptPasswordEncoder passwordEncoder) implements AppUserService {

    @Override
    public AppUser saveAppUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
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
    public Collection<SimpleGrantedAuthority> addRoleToUserByIdOrEmail(AddRoleRequest request) {
        try {
            Role role = roleRepo.findRoleByName(request.roleName())
                    .orElseThrow(() -> new IllegalStateException("Role not found"));
            AppUser appUser = appUserRepo.findAppUserByEmailOrId
                    (request.email(), request.userId()).orElseThrow(
                    () -> new IllegalStateException("User not found")
            );
            log.info("Adding role {} with request user {}",
                    role, request);
            return addRoleToUser(appUser, role);
        }
        catch (Exception e) {
            log.info("User with {} or role {} does not exist"
                    , request, request.roleName());
            throw new IllegalStateException("User or role does not exist");
        }
    }

    @Override
    public AppUser getUserById(String id) {
        log.info("Getting user {}", id);
        return appUserRepo
                .findAppUserById(id).orElseThrow(
                        () -> new IllegalStateException("User not found")
                );
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

    public AppUser getUserByEmail(String email) throws UsernameNotFoundException {
        Optional<AppUser> OptionalAppUser = appUserRepo.findAppUserByEmail(email);
        if (OptionalAppUser.isPresent()) {
            return OptionalAppUser.get();
        } else {
            log.info("User {} does not exist", email);
            throw new UsernameNotFoundException("User does not exist");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return  appUserRepo.findAppUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the DB"));
    }


    private Collection<SimpleGrantedAuthority> addRoleToUser(AppUser appUser, Role role) {
        appUser.addRole(role);
        appUserRepo.save(appUser);
        return appUser.getAuthorities();
    }

}
