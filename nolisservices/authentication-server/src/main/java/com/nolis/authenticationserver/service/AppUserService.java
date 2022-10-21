package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.modal.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface AppUserService extends UserDetailsService {
    AppUser saveAppUser(AppUser appUser);
    Role saveRole(Role role);
    AppUser getUserById(String id);
    AppUser getUserByEmail(String email);
    //Role getRoleById(String id);
    Collection<SimpleGrantedAuthority> addRoleToUserByIdOrEmail(AddRoleRequest request);

    List<AppUser> getUsers();
    List<Role> getRoles();


}
