package com.nolis.authenticationserver.controller;

import com.mongodb.lang.NonNull;
import com.nolis.authenticationserver.DTO.CustomHttpResponseDTO;
import com.nolis.authenticationserver.apihelper.ResponseHandler;
import com.nolis.authenticationserver.exception.InvalidTokenException;
import com.nolis.authenticationserver.exception.MissingAuthenticationException;
import com.nolis.authenticationserver.exception.TokenAuthenticationException;
import com.nolis.authenticationserver.exception.UnauthorizedTokenException;
import com.nolis.authenticationserver.security.JwtUtils;
import com.nolis.authenticationserver.service.JwtAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RestController @Slf4j @RequestMapping("/api/v1/auth")
public record AuthController(
        JwtUtils jwtUtils,
        JwtAuthenticationService jwtAuthenticationService,
        ResponseHandler responseHandler

) {
    @PostMapping("/authenticate")
    public ResponseEntity<CustomHttpResponseDTO> authenticateUser(
            @NonNull HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        log.info("Authorization Header {}", authorizationHeader);
        if (authorizationHeader != null && jwtUtils.tokenStartsWithPrefix(authorizationHeader)) {
            String token = jwtUtils.getTokenFromHeader(authorizationHeader);
            Map<String, Object> data = Map.of(
                    "access_token", token);
            try {
                if (jwtAuthenticationService.authenticateToken(token)) {
                    return responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("User authenticated successfully")
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            setupResponseHeaders(request));
                }
            } catch (InvalidTokenException e) {
                throw new TokenAuthenticationException("Invalid Token: " + e.getMessage());
            }
        }
        else {
            log.info("Authentication header must be provided");
            throw new MissingAuthenticationException("Authentication header must be provided");
        }
        return null;
    }

    //has authority
    @GetMapping("/has-authority")
    public ResponseEntity<CustomHttpResponseDTO> hasAuthority(
            @NonNull HttpServletRequest request) {
        String scope = request.getHeader("scope");
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        log.info("Authorization Header {} and scope {}", authorizationHeader, scope);
        if(scope == null ) {
            throw new UnauthorizedTokenException("Scope must be provided");
        }
        if (authorizationHeader != null && jwtUtils.tokenStartsWithPrefix(authorizationHeader)) {
            String token = jwtUtils.getTokenFromHeader(authorizationHeader);
            log.info("Token {}", token);
            Map<String, Object> data = Map.of(
                    "access_token", token);
                if (jwtAuthenticationService.isAuthorized(token, scope)) {
                    return responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("User authorized successfully")
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            setupResponseHeaders(request));
                } else {
                    throw new UnauthorizedTokenException("User is not authorized to access this resource", scope);
                }
        }
        else {
            log.info("Authorization header must be provided");
            throw new MissingAuthenticationException("Authorization header must be provided");
        }
    }

    //TODO check if token is expired and is refresh token
    @GetMapping("/token/refresh")
    private ResponseEntity<CustomHttpResponseDTO> refreshToken(@NonNull HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && jwtUtils.tokenStartsWithPrefix(authorizationHeader)) {
            try {
                String refreshToken = jwtUtils.getTokenFromHeader(authorizationHeader);
                return responseHandler.httpResponse(
                        CustomHttpResponseDTO.builder()
                                .message("Token refreshed successfully")
                                .data(jwtAuthenticationService
                                        .createTokenWitheRefreshToken(refreshToken))
                                .success(true)
                                .timestamp(System.currentTimeMillis())
                                .status(HttpStatus.OK)
                                .build(),
                        setupResponseHeaders(request));
            } catch (Exception e) {
                log.error("Error logging in : {}", e.getMessage()); //TODO change this to a http error handler
                return responseHandler.InternalServerErrorResponse(e);
            }
        }
        else {
            throw new MissingAuthenticationException("Authorization header must be provided");
        }
    }

    private HttpHeaders setupResponseHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, "application/json");
        headers.set(AUTHORIZATION, request.getHeader(AUTHORIZATION));
        headers.add("X-Prev-Path", request.getRequestURI());
        headers.add("X-Request-Path", request.getHeader("X-Request-Path"));
        headers.add("X-Request-Id", request.getHeader("X-Request-Id"));
        return headers;
    }

}
