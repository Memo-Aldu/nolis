package com.nolis.authenticationserver.service;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.modal.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AppUserService extends UserDetailsService {
    AppUser saveAppUser(AppUser appUser);
    Role saveRole(Role role);
    AppUser getUserById(String id);
    //Role getRoleById(String id);
    void addRoleToUserById(AddRoleRequest request);
    void addRoleToUserByEmail(AddRoleRequest request);
    List<AppUser> getUsers();
    List<Role> getRoles();


}
