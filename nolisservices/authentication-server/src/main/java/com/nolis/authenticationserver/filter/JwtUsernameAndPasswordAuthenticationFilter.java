package com.nolis.authenticationserver.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nolis.authenticationserver.DTO.EmailPasswordAuthenticationRequest;
import com.nolis.authenticationserver.apihelper.ResponseHandler;
import com.nolis.authenticationserver.model.AppUser;
import com.nolis.authenticationserver.configuration.JwtConfig;
import com.nolis.authenticationserver.security.JwtUtils;
import com.nolis.commondata.dto.CustomHttpResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final JwtUtils jwtUtils;
    private final ResponseHandler responseHandler;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        EmailPasswordAuthenticationRequest authenticationRequest = null;
        try {
            authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), EmailPasswordAuthenticationRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("\nEmail is : {}, Password is : {}",
                    authenticationRequest.email(), authenticationRequest.password());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.email(),
                    authenticationRequest.password()
            );
            return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        AppUser user = (AppUser) authResult.getPrincipal();
        String accessToken = jwtUtils.createToken(user);
        String refreshToken = jwtUtils.createRefreshToken(user);
        response = responseHandler.jsonResponse(
                    CustomHttpResponseDTO.builder()
                            .status(HttpStatus.OK)
                            .message("Login Successful")
                            .success(true)
                            .data(Map.of(
                                    jwtConfig.accessHeader(), accessToken,
                                    jwtConfig.refreshHeader(), refreshToken
                            ))
                            .timestamp(System.currentTimeMillis())
                            .build(), response);
        response.setHeader("refresh_token", refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response = responseHandler.jsonResponse(
                CustomHttpResponseDTO.builder()
                        .data(Map.of("error", "Invalid Authentication"))
                        .timestamp(System.currentTimeMillis())
                        .status(HttpStatus.UNAUTHORIZED)
                        .success(false)
                        .message("Invalid token")
                        .build(), response);
    }
}
