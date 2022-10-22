package com.nolis.authenticationserver.controller;

import com.mongodb.lang.NonNull;
import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.DTO.ResponseHandler;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.security.JwtConfig;
import com.nolis.authenticationserver.security.JwtUtils;
import com.nolis.authenticationserver.service.AppUserService;
import com.nolis.authenticationserver.service.JwtAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j @RequestMapping("/api/v1/auth")
@RestController
public record AppUserController(
        AppUserService appUserService,
        JwtAuthenticationService jwtAuthenticationService,
        JwtUtils jwtUtils,
        JwtConfig jwtConfig) {

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticateUser(@NonNull HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.tokenPrefix())) {
            try {
                if(jwtAuthenticationService.authenticateToken(authorizationHeader)) {
                    return ResponseHandler.httpResponse(
                            "Token authenticated successfully",
                            authorizationHeader, HttpStatus.OK,
                            true, getJwtHeaders(authorizationHeader));
                }
                else {
                    return ResponseHandler.httpBadResponse(
                            "authenticated failed",
                            HttpStatus.FORBIDDEN);
                }
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

    @GetMapping("/user")
    public ResponseEntity<Object> getUsers() {
        HttpHeaders headers = new HttpHeaders();
        return ResponseHandler.httpResponse(
                "Users fetched successfully",
                appUserService.getUsers(),
                HttpStatus.OK, true, headers);
    }

    @PostMapping("/user/save")
    public ResponseEntity<Object> getUsers(@Valid @RequestBody AppUser appUser) {
        HttpHeaders headers = new HttpHeaders();
        log.debug("Attempting to Save user {}", appUser);
        return ResponseHandler.httpResponse(
                    "User saved successfully",
                    appUserService.saveAppUser(appUser),
                    HttpStatus.CREATED, true, headers);
    }

    @PostMapping("/role/save")
    public ResponseEntity<Object> getUsers(@Valid @RequestBody Role role) {
        HttpHeaders headers = new HttpHeaders();
        log.debug("Attempting to save role {}", role);
        return ResponseHandler.httpResponse(
                "Role saved successfully",
                appUserService.saveRole(role), HttpStatus.CREATED,
                true, headers);
    }

    @GetMapping("/role")
    public ResponseEntity<Object> getRoles() {
        HttpHeaders headers = new HttpHeaders();
        log.debug("Attempting to fetch all roles");
        return ResponseHandler.httpResponse(
                "Role fetched successfully",
                appUserService.getRoles(),
                HttpStatus.OK, true, headers);
    }

    @PatchMapping("/user/addrole")
    public ResponseEntity<Object> addRoleToUser(@Valid @RequestBody AddRoleRequest request) {
        HttpHeaders headers = new HttpHeaders();
        log.info("Attempting to add role {} with request {}", request.roleName(), request);
        if(request.userId() != null || request.email() != null
                && request.roleName() != null) {
            log.info("Adding role {} to user {}", request.roleName(), request.userId());
            appUserService.addRoleToUserByIdOrEmail(request);
            return ResponseHandler.httpResponse(
                    "Role added successfully",
                    appUserService.addRoleToUserByIdOrEmail(request),
                    HttpStatus.OK, true, headers);
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
                return ResponseHandler.httpResponse(
                        "Token refreshed successfully",
                        jwtAuthenticationService.createTokenWitheRefreshToken(refreshToken),
                        HttpStatus.OK, true,getJwtHeaders(authorizationHeader));
            } catch (Exception e) {
                log.error("Error logging in : {}", e.getMessage()); //TODO change this to a http error handler
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


    private HttpHeaders getJwtHeaders(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorizationHeader);
        return headers;
    }





}
