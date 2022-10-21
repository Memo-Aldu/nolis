package com.nolis.authenticationserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.lang.NonNull;
import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.security.JwtConfig;
import com.nolis.authenticationserver.security.JwtUtils;
import com.nolis.authenticationserver.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Slf4j @RequestMapping("/api/v1/auth")
@RestController
public record AppUserController(
        AppUserService appUserService,
        JwtUtils jwtUtils,
        JwtConfig jwtConfig) {

    @GetMapping("/user")
    private ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok(appUserService.getUsers());
    }

    @PostMapping("/user/save")
    private ResponseEntity<AppUser> getUsers(@Valid @RequestBody AppUser appUser) {
        log.debug("Attempting to Saving user {}", appUser);
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


    @PatchMapping("/user/addrole")
    private ResponseEntity<?> addRoleToUser(@Valid @RequestBody AddRoleRequest request) {
        if(request.userId() != null) {
            appUserService.addRoleToUserById(request);
            return ResponseEntity.ok().build();
        } else if (request.email() != null) {
            appUserService.addRoleToUserByEmail(request);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("User id or email is required to add role");
    }
    @GetMapping("/token/refresh")
    private void refreshToken(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) throws IOException {
       String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.tokenPrefix())) {
            try {
                String refreshToken = authorizationHeader.substring(jwtConfig.tokenPrefix().length());
                String newToken = jwtUtils.createTokenWithRefreshToken(refreshToken, request);
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put(jwtConfig.accessHeader(), newToken);
                responseBody.put(jwtConfig.refreshHeader(), refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
            } catch (Exception e) {
                log.error("Error logging in : {}", e.getMessage()); //TODO change this to a http error handler
                //response.setHeader("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);

            }

        }
        else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Authorization header must be provided");
        }
    }





}
