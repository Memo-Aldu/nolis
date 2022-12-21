package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.DTO.AppUserRequest;
import com.nolis.authenticationserver.model.AppUser;
import com.nolis.authenticationserver.model.Role;
import com.nolis.authenticationserver.repository.AppUserRepo;
import com.nolis.authenticationserver.repository.RoleRepo;
import com.nolis.commondata.exception.AppEntityAlreadyExistException;
import com.nolis.commondata.exception.AppEntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service @Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames={"AppUsers"})
public class AppUserServiceImp implements AppUserService {
    private final AppUserRepo appUserRepo;
    private final RoleRepo roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    static final Role defaultRole = new Role("ROLE_APP_USER");

    @Override
    @Caching(put = {
            //not caching id because it is not created yet
                    @CachePut(key = "#appUser.email")}
    )
    public AppUser saveAppUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        log.info("Saving new user {} to the database", appUser.toString());
        Optional<AppUser> OptionalAppUser = appUserRepo.findAppUserByEmail(
                appUser.getEmail()
        );
        if (OptionalAppUser.isPresent()) {
            log.info("User {} already exists", appUser.getEmail());
            throw new AppEntityAlreadyExistException("User already exists");
        }
        // if appUser does not have the default role
        if (!appUser.getAuthorities().contains(defaultRole)) {
            appUser.addRole(defaultRole);
            log.info("Adding default role to user {}", appUser.getEmail());
        }
        return appUserRepo.save(appUser);
    }

    @Override //not used yet
    public AppUser getAppUserByIdOrEmail(AppUserRequest request) {

        log.info("Getting user {}", request);
        return appUserRepo.findAppUserByEmailOrId(
                request.email(), request.id()).orElseThrow(
                () -> new AppEntityNotFoundException("User not found, request: " + request)
        );
    }

    @Override
    @Caching(evict = {
            // clearing the cache for the user with the id or email
                    @CacheEvict(key = "#request.email()", condition = "#request.email() != null"),
                    @CacheEvict(key = "#request.userId()", condition = "#request.userId() != null")}
    )
    public Collection<Role> addRoleToUserByIdOrEmail(AddRoleRequest request) {
        Role role = roleRepo.findRoleByAuthority(request.authority())
                .orElseThrow(() -> new AppEntityNotFoundException("Role does not exist, " +
                        "role: " + request.authority()));
        AppUser appUser = appUserRepo.findAppUserByEmailOrId
                (request.email(), request.userId()).orElseThrow(
                () -> new AppEntityNotFoundException("User does not exist, " +
                        "userId: " + request.userId() + ", email: " + request.email())
        );
        if(appUser.getAuthorities().contains(role)) {
            throw new AppEntityAlreadyExistException("User already has this role");
        }
        log.info("Adding role {} with request user {}",
                role, request);
        return addRoleToUser(appUser, role);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Getting all users");
        return appUserRepo.findAll();
    }

    @Override
    public Page<AppUser> getUsers(Pageable page) {
        log.info("Getting a page of users");
        return appUserRepo.findAll(page);
    }

    @Override
    @Cacheable(key = "#email")
    public AppUser getUserByEmail(String email) {
        log.info("Getting user by email");
        return appUserRepo.findAppUserByEmail(email).orElseThrow(
                () -> new AppEntityNotFoundException("User not found with email " + email)
        );
    }

    @Override
    @Cacheable(key = "#request.email()", unless = "#request.email() == null")
    public AppUser getAppUserByIdOrEmailAndPassword(AppUserRequest request) {
        log.info("Getting user by id or email and password");
        AppUser user = appUserRepo.findAppUserByEmailOrId(
                request.email(), request.id()).orElseThrow(
                () -> new AppEntityNotFoundException("User not found, request: " + request));
        if(passwordDoesNotMatch(request.password(), user.getPassword())) {
            log.warn("User {} does not exist", request);
            throw new AppEntityNotFoundException("User does not exist, request: " + request);
        }
        return user;
    }

    @Override
    @CacheEvict(key = "#request.email()")
    public void deleteUserByIdOrEmail(AppUserRequest request) {
        //delete the user and check if the user exists
        log.info("Deleting user {}", request);
        AppUser user = appUserRepo.findAppUserByEmailOrId(
                request.email(), request.id()).orElseThrow(
                () -> new AppEntityNotFoundException("User not found, request: " + request)
        );
        if(passwordDoesNotMatch(request.password(), user.getPassword())) {
            log.warn("User {} does not exist", request);
            throw new AppEntityNotFoundException("User does not exist, request: " + request);
        }
        else {
            appUserRepo.deleteAppUserByEmailOrId(request.email(), request.id());
        }
    }

    @Override
    @Cacheable(key = "#email")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Getting user by email");
        return  appUserRepo.findAppUserByEmail(email)
                .orElseThrow(() -> new AppEntityNotFoundException
                        ("User with email " + email + "not found in the DB"));
    }


    private Collection<Role> addRoleToUser(AppUser appUser, Role role) {
        appUser.addRole(role);
        appUserRepo.save(appUser);
        return appUser.getAuthorities();
    }

    private boolean passwordDoesNotMatch(String password, String encodedPassword) {
        return !passwordEncoder.matches(password, encodedPassword);
    }

}
