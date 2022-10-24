package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.DTO.AppUserRequest;
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
    public AppUser getAppUserByIdOrEmail(AppUserRequest request) {

        log.info("Getting user {}", request);
        return appUserRepo.findAppUserByEmailOrId(
                request.email(), request.id()).orElseThrow(
                () -> new IllegalStateException("User not found")
        );
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
    public List<AppUser> getUsers() {
        log.info("Getting all users");
        return appUserRepo.findAll();
    }

    @Override
    public AppUser getUserByEmail(String email) {
        log.info("Getting user by email");
        return appUserRepo.findAppUserByEmail(email).orElseThrow(
                () -> new IllegalStateException("User not found")
        );
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
