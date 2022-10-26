package com.nolis.authenticationserver.filter;
import com.mongodb.lang.NonNull;
import com.nolis.authenticationserver.DTO.CustomHttpResponseDTO;
import com.nolis.authenticationserver.apihelper.ResponseHandler;
import com.nolis.authenticationserver.security.EndpointConfig;
import com.nolis.authenticationserver.security.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@AllArgsConstructor @Slf4j @Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final EndpointConfig endpointConfig;
    private final ResponseHandler responseHandler;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if(!endpointConfig.isSecured.test(request)) {
            log.info("Open endpoint : {}", request.getServletPath());
            filterChain.doFilter(request, response);
        }
        else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            response.setHeader(AUTHORIZATION, authorizationHeader);
            if(authorizationHeader != null && jwtUtils.tokenStartsWithPrefix(authorizationHeader)) {
                try {
                    Authentication authentication = jwtUtils.authenticateToken(
                            jwtUtils.getTokenFromHeader(authorizationHeader));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                }
                catch (Exception e){
                        response = responseHandler.jsonResponse(
                            CustomHttpResponseDTO.builder()
                                    .data(Map.of("error", "Invalid token"))
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.UNAUTHORIZED)
                                    .success(false)
                                    .message(e.getMessage())
                                    .build(), response);
                }
            }
            else {
                 response = responseHandler.jsonResponse(
                        CustomHttpResponseDTO.builder()
                                .timestamp(System.currentTimeMillis())
                                .data(Map.of("error", "Missing " +
                                        "authentication header"))

                                .message("Missing authorization header")
                                .status(HttpStatus.BAD_REQUEST)
                                .success(false)
                                .data(new HashMap<>())
                                .build(), response);
            }
        }
    }
}
