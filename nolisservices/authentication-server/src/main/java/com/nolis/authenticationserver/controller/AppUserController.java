package com.nolis.authenticationserver.controller;

import com.nolis.authenticationserver.DTO.AddRoleRequest;
import com.nolis.authenticationserver.DTO.AppUserRequest;
import com.nolis.authenticationserver.DTO.CustomHttpResponseDTO;
import com.nolis.authenticationserver.apihelper.ResponseHandler;
import com.nolis.authenticationserver.exception.BadRequestException;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

@Slf4j @RequestMapping("/api/v1/auth/user")
@RestController
public record AppUserController(
        AppUserService appUserService,
        ResponseHandler responseHandler) {

    @GetMapping("/getall")
    public ResponseEntity<CustomHttpResponseDTO> getAppUsers() {
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

    @GetMapping("/get")
    public ResponseEntity<CustomHttpResponseDTO> getAppUser(
            AppUserRequest request) {
        if(request.isValid()) {
            HttpHeaders headers = new HttpHeaders();
            Map<String, Object> data = Map.of(
                    "user", appUserService.getAppUserByIdOrEmail(request)
            );
            return responseHandler.httpResponse(
                    CustomHttpResponseDTO.builder()
                            .message("User fetched successfully")
                            .data(data)
                            .success(true)
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.OK)
                            .build(),
                    headers);
        } else {
            throw new BadRequestException("Invalid request body for "+request);
        }
    }


    @PostMapping("/save")
    public ResponseEntity<CustomHttpResponseDTO> getUsers(@RequestBody AppUser appUser) {
        if(!appUser.isValidEntity()) {
            throw new BadRequestException("Invalid request body for "+appUser);
        }
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

    @PatchMapping("/addrole")
    public ResponseEntity<CustomHttpResponseDTO> addRoleToUser(@Valid @RequestBody AddRoleRequest request) {
        if(!request.isValid()) {
            throw new BadRequestException("Invalid request body for "+request);
        }
        HttpHeaders headers = new HttpHeaders();
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
}
