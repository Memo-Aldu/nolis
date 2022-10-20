package com.nolis.authenticationserver.controller;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Slf4j @RequestMapping("/api/v1/auth")
@RestController
public record AppUserController(AppUserService appUserService) {

    @GetMapping("/user")
    private ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok(appUserService.getUsers());
    }

    @PostMapping("/user/save")
    private ResponseEntity<AppUser> getUsers(@Valid @RequestBody AppUser appUser) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/user/save").toUriString());
        log.info("Saving new user {} to the database", appUser.toString());
        return ResponseEntity.created(uri)
                .body(appUserService.saveAppUser(appUser));
    }

    @PostMapping("/role/save")
    private ResponseEntity<Role> getUsers(@Valid @RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/role/save").toUriString());
        return ResponseEntity.created(uri).body(appUserService.saveRole(role));
    }

    @GetMapping("/role")
    private ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(appUserService.getRoles());
    }


    @PutMapping("/user/addrole")
    private ResponseEntity<?> addRoleToUser(@Valid @RequestBody AddRoleRequest request) {
        appUserService.addRoleToUserById(request);
        return ResponseEntity.ok().build();
    }





}
