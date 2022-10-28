package com.nolis.authenticationserver.controller;

import com.nolis.authenticationserver.DTO.CustomHttpResponseDTO;
import com.nolis.authenticationserver.DTO.RoleRequest;
import com.nolis.authenticationserver.apihelper.ResponseHandler;
import com.nolis.authenticationserver.exception.BadRequestException;
import com.nolis.authenticationserver.modal.Role;
import com.nolis.authenticationserver.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController @Slf4j @RequestMapping("/api/v1/auth/role")
public record RoleController(
        RoleService roleService,
        ResponseHandler responseHandler) {

    @PostMapping("/save")
    public ResponseEntity<CustomHttpResponseDTO> saveRole(@RequestBody Role role) {
        if(!role.isValidEntity()) {
            throw new BadRequestException("Invalid request body for "+role);
        }
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> data = Map.of(
                "role",
                roleService.saveRole(role)
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

    @GetMapping("/get-all")
    public ResponseEntity<CustomHttpResponseDTO> getRoles() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> data = Map.of(
                "roles", roleService.getRoles()
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

    @GetMapping("/get-page")
    public ResponseEntity<CustomHttpResponseDTO> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> data = Map.of(
                "roles", roleService.getRoles(pageable)
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

    @GetMapping("/get")
    public ResponseEntity<CustomHttpResponseDTO> getRole(@RequestBody RoleRequest request) {
        if(!request.isValid()) {
            throw new BadRequestException("Invalid request body for "+request);
        }
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> data = Map.of(
                "role", roleService.getRoleByIdOrName(request)
        );
        log.debug("Attempting to fetch role {}", request);
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .message("Role fetched successfully")
                        .data(data)
                        .success(true)
                        .timestamp(System.currentTimeMillis())
                        .status(HttpStatus.OK)
                        .build(),
                headers);
    }

    // Does not delete the role from the user
    @DeleteMapping("/delete")
    public ResponseEntity<CustomHttpResponseDTO> deleteRole(@RequestBody RoleRequest request) {
        HttpHeaders headers = new HttpHeaders();
        try {
            if(!request.isValid()) {
                throw new BadRequestException("Invalid request body for "+request);
            }
            roleService.deleteRoleByIdOrName(request);
            log.debug("Attempting to delete role {}", request);
            return responseHandler.httpResponse(
                    CustomHttpResponseDTO.builder()
                            .message("Role deleted successfully")
                            .data(new HashMap<>())
                            .success(true)
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.OK)
                            .build(),
                    headers);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());

        }


    }
}
