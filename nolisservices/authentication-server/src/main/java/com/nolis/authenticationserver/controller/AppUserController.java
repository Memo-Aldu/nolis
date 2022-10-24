package com.nolis.authenticationserver.controller;

import com.mongodb.lang.NonNull;
import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.DTO.CustomHttpResponseDTO;
import com.nolis.authenticationserver.apihelper.ResponseHandler;
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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j @RequestMapping("/api/v1/auth")
@RestController
public record AppUserController(
        AppUserService appUserService,
        JwtAuthenticationService jwtAuthenticationService,
        JwtUtils jwtUtils,
        JwtConfig jwtConfig,
        ResponseHandler responseHandler) {

    @PostMapping("/authenticate")
    public ResponseEntity<CustomHttpResponseDTO> authenticateUser(
            @NonNull HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        log.info("Authorization Header {}", authorizationHeader);
        if(authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.tokenPrefix())) {
            try {
                Map<String, Object> data = Map.of(
                        "token", authorizationHeader);
                if(jwtAuthenticationService.authenticateToken(authorizationHeader)) {
                    return responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("User authenticated")
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            addJwtHeaders(authorizationHeader));
                }
                else {
                    log.info("Token authentication failed");
                    return responseHandler.httpBadResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("Token authentication failed")
                                    .data(data)
                                    .success(false)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.UNAUTHORIZED)
                                    .build());
                }
            } catch (Exception e) {
                log.error("Error logging in : {}", e.getMessage()); //TODO change this to a http error handler
                //response.setHeader("error", e.getMessage());
                return responseHandler.httpBadResponse(
                        CustomHttpResponseDTO.builder()
                                .message(e.getMessage())
                                .data(new HashMap<>())
                                .success(false)
                                .timestamp(System.currentTimeMillis())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build());
            }

        }
        else {
            log.info("Authorization header must be provided");
            return responseHandler.httpBadResponse(
                    CustomHttpResponseDTO.builder()
                            .message("Authorization header must be provided")
                            .success(false)
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.FORBIDDEN)
                            .build());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<CustomHttpResponseDTO> getUsers() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> data = Map.of(
                "users", appUserService.getUsers()
        );
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .message("Users fetched successfully")
                        .data(data)
                        .success(true)
                        .timestamp(System.currentTimeMillis())
                        .status(HttpStatus.OK)
                        .build(),
                headers);
    }

    @PostMapping("/user/save")
    public ResponseEntity<CustomHttpResponseDTO> getUsers(@Valid @RequestBody AppUser appUser) {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> data = Map.of(
                "user",
                appUserService.saveAppUser(appUser)
        );
        log.debug("Attempting to Save user {}", appUser);
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .message("User saved successfully")
                        .data(data)
                        .success(true)
                        .timestamp(System.currentTimeMillis())
                        .status(HttpStatus.CREATED)
                        .build(),
                headers);
    }

    @PostMapping("/role/save")
    public ResponseEntity<CustomHttpResponseDTO> getUsers(@Valid @RequestBody Role role) {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> data = Map.of(
                "role",
                appUserService.saveRole(role)
        );
        log.debug("Attempting to save role {}", role);
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .message("Role saved successfully")
                        .data(data)
                        .success(true)
                        .timestamp(System.currentTimeMillis())
                        .status(HttpStatus.CREATED)
                        .build(),
                headers);
    }

    @GetMapping("/role")
    public ResponseEntity<CustomHttpResponseDTO> getRoles() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> data = Map.of(
                "roles", appUserService.getRoles()
        );
        log.debug("Attempting to fetch all roles");
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .message("Roles fetched successfully")
                        .data(data)
                        .success(true)
                        .timestamp(System.currentTimeMillis())
                        .status(HttpStatus.OK)
                        .build(),
                headers);
    }

    @PatchMapping("/user/addrole")
    public ResponseEntity<CustomHttpResponseDTO> addRoleToUser(@Valid @RequestBody AddRoleRequest request) {
        HttpHeaders headers = new HttpHeaders();
        log.info("Attempting to add role {} with request {}"
                ,request.roleName(), request);
        if(request.userId() != null || request.email() != null
                && request.roleName() != null) {
            log.info("Adding role {} to user {}", request.roleName(), request.userId());
            Map<String, Object> data = Map.of(
                    "user", appUserService
                            .addRoleToUserByIdOrEmail(request)
            );
            log.debug("Attempting to fetch all roles");
            return responseHandler.httpResponse(
                    CustomHttpResponseDTO.builder()
                            .message("Role added successfully")
                            .data(data)
                            .success(true)
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.OK)
                            .build(),
                    headers);
        }
        else {
            log.info("User id or email is null");
            return responseHandler.httpBadResponse(
                    CustomHttpResponseDTO.builder()
                            .message("Bad request - User id or email is null\"")
                            .success(false)
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
    }

    @GetMapping("/token/refresh")
    private ResponseEntity<CustomHttpResponseDTO> refreshToken(@NonNull HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.tokenPrefix())) {
            try {
                String refreshToken = authorizationHeader
                        .substring(jwtConfig.tokenPrefix().length());
                Map<String, Object> data = Map.of(
                        "token", jwtAuthenticationService
                                .createTokenWitheRefreshToken(refreshToken),
                        "refresh_token", refreshToken
                );
                return responseHandler.httpResponse(
                        CustomHttpResponseDTO.builder()
                                .message("Token refreshed successfully")
                                .data(data)
                                .success(true)
                                .timestamp(System.currentTimeMillis())
                                .status(HttpStatus.OK)
                                .build(),
                        new HttpHeaders());
            } catch (Exception e) {
                log.error("Error logging in : {}", e.getMessage()); //TODO change this to a http error handler
                return responseHandler.httpBadResponse(
                        CustomHttpResponseDTO.builder()
                                .message("Error logging in : " + e.getMessage())
                                .success(false)
                                .timestamp(System.currentTimeMillis())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build());
            }
        }
        else {
            return responseHandler.httpBadResponse(
                    CustomHttpResponseDTO.builder()
                            .message("Authorization header must be provided")
                            .success(false)
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.FORBIDDEN)
                            .build());
        }
    }


    private HttpHeaders addJwtHeaders(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorizationHeader);
        return headers;
    }
}
