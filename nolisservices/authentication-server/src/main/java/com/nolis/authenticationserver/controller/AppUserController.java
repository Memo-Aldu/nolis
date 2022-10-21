package com.nolis.authenticationserver.controller;

import com.mongodb.lang.NonNull;
import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.DTO.ResponseHandler;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.security.JwtConfig;
import com.nolis.authenticationserver.security.JwtUtils;
import com.nolis.authenticationserver.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j @RequestMapping("/api/v1/auth")
@RestController
public record AppUserController(
        AppUserService appUserService,
        JwtUtils jwtUtils,
        JwtConfig jwtConfig) {

    @GetMapping("/user")
    public ResponseEntity<Object> getUsers() {
        return ResponseHandler.httpResponse(
                "Users fetched successfully",
                appUserService.getUsers(),
                HttpStatus.OK,
                true);
    }

    @PostMapping("/user/save")
    public ResponseEntity<Object> getUsers(@Valid @RequestBody AppUser appUser) {
        log.debug("Attempting to Save user {}", appUser);
        return ResponseHandler.httpResponse(
                    "User saved successfully",
                    appUserService.saveAppUser(appUser),
                    HttpStatus.CREATED,
                    true);
    }

    @PostMapping("/role/save")
    public ResponseEntity<Object> getUsers(@Valid @RequestBody Role role) {
        log.debug("Attempting to save role {}", role);
        return ResponseHandler.httpResponse(
                "Role saved successfully",
                appUserService.saveRole(role),
                HttpStatus.CREATED,
                true);
    }

    @GetMapping("/role")
    public ResponseEntity<Object> getRoles() {
        log.debug("Attempting to fetch all roles");
        return ResponseHandler.httpResponse(
                "Role fetched successfully",
                appUserService.getRoles(),
                HttpStatus.OK,
                true);
    }

    @PatchMapping("/user/addrole")
    public ResponseEntity<Object> addRoleToUser(@Valid @RequestBody AddRoleRequest request) {
        log.info("Attempting to add role {} with request {}", request.roleName(), request);
        if(request.userId() != null || request.email() != null
                && request.roleName() != null) {
            log.info("Adding role {} to user {}", request.roleName(), request.userId());
            appUserService.addRoleToUserByIdOrEmail(request);
            return ResponseHandler.httpResponse(
                    "Role added successfully",
                    appUserService.addRoleToUserByIdOrEmail(request),
                    HttpStatus.OK,
                    true);
        } else {
            log.info("User id or email is null");
            return ResponseHandler.httpBadResponse(
                    "Bad request - User id or email is null",
                    HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/token/refresh")
    private ResponseEntity<Object> refreshToken(@NonNull HttpServletRequest request) {
       String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.tokenPrefix())) {
            try {
                String refreshToken = authorizationHeader.substring(jwtConfig.tokenPrefix().length());
                String newToken = jwtUtils.createTokenWithRefreshToken(refreshToken, request);
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put(jwtConfig.accessHeader(), newToken);
                responseBody.put(jwtConfig.refreshHeader(), refreshToken);
                return ResponseHandler.httpResponse(
                        "Token refreshed successfully",
                        responseBody,
                        HttpStatus.OK,
                        true);

            } catch (Exception e) {
                log.error("Error logging in : {}", e.getMessage()); //TODO change this to a http error handler
                //response.setHeader("error", e.getMessage());
                return ResponseHandler.httpBadResponse(
                        "Error logging in : " + e.getMessage(),
                        HttpStatus.FORBIDDEN);
            }

        }
        else {
            return ResponseHandler.httpBadResponse(
                    "Authorization header must be provided",
                    HttpStatus.FORBIDDEN);
        }
    }





}
