package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.DTO.AppUserRequest;
import com.nolis.authenticationserver.modal.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface AppUserService extends UserDetailsService {
    AppUser saveAppUser(AppUser appUser);
    AppUser getAppUserByIdOrEmail(AppUserRequest request);
    Collection<SimpleGrantedAuthority> addRoleToUserByIdOrEmail(AddRoleRequest request);
    List<AppUser> getUsers();
    Page<AppUser> getUsers(Pageable page);
    AppUser getUserByEmail(String email);
}
